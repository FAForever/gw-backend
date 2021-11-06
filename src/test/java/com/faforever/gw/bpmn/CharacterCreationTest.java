package com.faforever.gw.bpmn;

import com.faforever.gw.bpmn.services.CharacterCreationService;
import com.google.common.collect.ImmutableMap;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.junit5.test.ProcessEngineExtension;
import org.camunda.bpm.extension.mockito.DelegateExpressions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.execute;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.job;

@ExtendWith(ProcessEngineExtension.class)
public class CharacterCreationTest {

    public ProcessEngine processEngine;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DelegateExpressions.registerJavaDelegateMock("checkUserCharactersTask");
        DelegateExpressions.registerJavaDelegateMock("generateNameProposalTask");
        DelegateExpressions.registerJavaDelegateMock("characterNameProposalNotification");
        DelegateExpressions.registerJavaDelegateMock("createCharacterTask");
        DelegateExpressions.registerJavaDelegateMock("helloNotification");
        DelegateExpressions.registerJavaDelegateMock("characterJoinedGwNotification");
        DelegateExpressions.registerJavaDelegateMock("userAckMessage");
        DelegateExpressions.registerJavaDelegateMock("userErrorMessage");
    }

    @AfterEach
    public void tearDown() {
        Mocks.reset();
    }

    @Test
    @Deployment(resources = "bpmn/character_creation.bpmn")
    public void errorHasActiveCharacter() throws Exception {
        DelegateExpressions.registerJavaDelegateMock("checkUserCharactersTask")
                .onExecutionSetVariables(ImmutableMap.of("hasActiveCharacter", true));

        ProcessInstance processInstance = startProcess();

        assertThat(processInstance).hasPassedInOrder(
                "StartEvent_UserRequestsCharacter",
                "ServiceTask_CheckActiveAndDeadCharacters",
                "ExclusiveGateway_HasActiveCharacter",
                "EndEvent_Error_ActiveCharactersExists"
        )
                .isEnded();
    }

    @Test
    @Deployment(resources = "bpmn/character_creation.bpmn")
    public void errorHasDeadCharacterAndFactionMismatch() throws Exception {
        DelegateExpressions.registerJavaDelegateMock("checkUserCharactersTask")
                .onExecutionSetVariables(ImmutableMap.of("hasActiveCharacter", false, "hasDeadCharacter", true, "factionMatches", false));

        ProcessInstance processInstance = startProcess();

        assertThat(processInstance).hasPassedInOrder(
                "StartEvent_UserRequestsCharacter",
                "ServiceTask_CheckActiveAndDeadCharacters",
                "ExclusiveGateway_HasActiveCharacter",
                "ExclusiveGateway_HasDeadCharacter",
                "ExclusiveGateway_FactionMatches",
                "EndEvent_Error_FactionUnchangeable"
        )
                .isEnded();
    }

    @Test
    @Deployment(resources = "bpmn/character_creation.bpmn")
    public void errorHasDeadCharacterAndFactionMatchAndInvalidSelection() throws Exception {
        DelegateExpressions.registerJavaDelegateMock("checkUserCharactersTask")
                .onExecutionSetVariables(ImmutableMap.of("hasActiveCharacter", false, "hasDeadCharacter", true, "factionMatches", true));
        DelegateExpressions.registerJavaDelegateMock("createCharacterTask").onExecutionThrowBpmnError("3003", "Your chosen character name was not offered");


        ProcessInstance processInstance = startProcess();

        assertThat(processInstance).hasPassedInOrder(
                "StartEvent_UserRequestsCharacter",
                "ServiceTask_CheckActiveAndDeadCharacters",
                "ExclusiveGateway_HasActiveCharacter",
                "ExclusiveGateway_HasDeadCharacter",
                "ExclusiveGateway_FactionMatches",
                "ExclusiveGateway_MergeGenerate",
                "ServiceTask_GenerateProposal",
                "IntermediateThrowEvent_SendNameProposal"

        )
                .isWaitingAt("ReceiveTask_ReceiveNameSelection");

        processEngine.getRuntimeService().correlateMessage(CharacterCreationService.RECEIVE_CHARACTER_NAME_SELECTION_MESSAGE, "test");

        assertThat(processInstance).hasPassedInOrder(
                "IntermediateThrowEvent_SendNameProposal",
                "ReceiveTask_ReceiveNameSelection",
                "ServiceTask_CreateCharacter",
                "BoundaryEvent_Error_InvalidSelection",
                "EndEvent_Error_InvalidSelection"
        )
                .isEnded();
    }

    @Test
    @Deployment(resources = "bpmn/character_creation.bpmn")
    public void errorTimeoutOnNameSelection() throws Exception {
        DelegateExpressions.registerJavaDelegateMock("checkUserCharactersTask")
                .onExecutionSetVariables(ImmutableMap.of("hasActiveCharacter", false, "hasDeadCharacter", true, "factionMatches", true));
        DelegateExpressions.registerJavaDelegateMock("createCharacterTask").onExecutionThrowBpmnError("3003", "Your chosen character name was not offered");


        ProcessInstance processInstance = startProcess();

        assertThat(processInstance).hasPassedInOrder(
                "StartEvent_UserRequestsCharacter",
                "ServiceTask_CheckActiveAndDeadCharacters",
                "ExclusiveGateway_HasActiveCharacter",
                "ExclusiveGateway_HasDeadCharacter",
                "ExclusiveGateway_FactionMatches",
                "ExclusiveGateway_MergeGenerate",
                "ServiceTask_GenerateProposal",
                "IntermediateThrowEvent_SendNameProposal"

        )
                .isWaitingAt("ReceiveTask_ReceiveNameSelection");

        ClockUtil.offset(60 * 11 * 1000L);
        execute(job());

        assertThat(processInstance).hasPassedInOrder(
                "IntermediateThrowEvent_SendNameProposal",
                "ReceiveTask_ReceiveNameSelection",
                "BoundaryEvent_Error_Timeout",
                "EndEvent_Error_Timeout"
        )
                .isEnded();
    }

    @Test
    @Deployment(resources = "bpmn/character_creation.bpmn")
    public void success() throws Exception {
        DelegateExpressions.registerJavaDelegateMock("checkUserCharactersTask")
                .onExecutionSetVariables(ImmutableMap.of("hasActiveCharacter", false, "hasDeadCharacter", true, "factionMatches", true));

        ProcessInstance processInstance = startProcess();

        assertThat(processInstance).hasPassedInOrder(
                "StartEvent_UserRequestsCharacter",
                "ServiceTask_CheckActiveAndDeadCharacters",
                "ExclusiveGateway_HasActiveCharacter",
                "ExclusiveGateway_HasDeadCharacter",
                "ExclusiveGateway_FactionMatches",
                "ExclusiveGateway_MergeGenerate",
                "ServiceTask_GenerateProposal",
                "IntermediateThrowEvent_SendNameProposal"

        )
                .isWaitingAt("ReceiveTask_ReceiveNameSelection");

        processEngine.getRuntimeService().correlateMessage(CharacterCreationService.RECEIVE_CHARACTER_NAME_SELECTION_MESSAGE, "test");

        assertThat(processInstance).hasPassedInOrder(
                "IntermediateThrowEvent_SendNameProposal",
                "ReceiveTask_ReceiveNameSelection",
                "ServiceTask_CreateCharacter")
                .isWaitingAtExactly("IntermediateThrowEvent_Ack");

        execute(job());

        assertThat(processInstance).hasPassedInOrder(
                "ServiceTask_CreateCharacter",
                "IntermediateThrowEvent_Ack",
                "IntermediateThrowEvent_Hello",
                "EndEvent_CharacterCreated"
        )
                .isEnded();
    }


    private ProcessInstance startProcess() {
        return processEngine.getRuntimeService().startProcessInstanceByMessage(CharacterCreationService.REQUEST_CHARACTER_MESSAGE, "test");
    }
}
