package com.faforever.gw.bpmn;

import com.faforever.gw.bpmn.services.CharacterCreationService;
import com.google.common.collect.ImmutableMap;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.mockito.DelegateExpressions;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.execute;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.job;

public class CharacterCreationTest {

    @Rule
    public ProcessEngineRule processEngineRule = new ProcessEngineRule();

    @Before
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

    @After
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
                .isWaitingAt("IntermediateCatchEvent_ReceiveNameSelection");

        processEngineRule.getRuntimeService().correlateMessage(CharacterCreationService.RECEIVE_CHARACTER_NAME_SELECTION_MESSAGE, "test");

        assertThat(processInstance).hasPassedInOrder(
                "IntermediateThrowEvent_SendNameProposal",
                "IntermediateCatchEvent_ReceiveNameSelection",
                "ServiceTask_CreateCharacter",
                "BoundaryEvent_Error_InvalidSelection",
                "EndEvent_Error_InvalidSelection"
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
                .isWaitingAt("IntermediateCatchEvent_ReceiveNameSelection");

        processEngineRule.getRuntimeService().correlateMessage(CharacterCreationService.RECEIVE_CHARACTER_NAME_SELECTION_MESSAGE, "test");

        assertThat(processInstance).hasPassedInOrder(
                "IntermediateThrowEvent_SendNameProposal",
                "IntermediateCatchEvent_ReceiveNameSelection",
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
        return processEngineRule.getRuntimeService().startProcessInstanceByMessage(CharacterCreationService.REQUEST_CHARACTER_MESSAGE, "test");
    }
}
