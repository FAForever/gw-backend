package com.faforever.gw.bpmn;

import com.faforever.gw.bpmn.services.PlanetaryAssaultService;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.GameResult;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Planet;
//import jersey.repackaged.com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.extension.mockito.DelegateExpressions;
import org.camunda.bpm.extension.mockito.mock.FluentJavaDelegateMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.*;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.assertThat;
import static org.camunda.bpm.extension.mockito.DelegateExpressions.verifyJavaDelegateMock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;


public class PlanetaryAssaultTest {

    @Rule
    public ProcessEngineRule processEngineRule = new ProcessEngineRule();


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DelegateExpressions.registerJavaDelegateMock("initiateAssaultTask").onExecutionSetVariables(
                Variables.createVariables()
                        .putValue("battle", UUID.randomUUID())
        );
        DelegateExpressions.registerJavaDelegateMock("planetUnderAssaultMessage");
        DelegateExpressions.registerJavaDelegateMock("userAckMessage");
        DelegateExpressions.registerJavaDelegateMock("userErrorMessage");
        DelegateExpressions.registerJavaDelegateMock("addCharacterToAssaultTask");
        DelegateExpressions.registerJavaDelegateMock("battleParticipantJoinedAssaultMessage");
        DelegateExpressions.registerJavaDelegateMock("removeCharacterFromAssaultTask");
        DelegateExpressions.registerJavaDelegateMock("battleParticipantLeftAssaultMessage");
        DelegateExpressions.registerJavaDelegateMock("calculateWaitingProgressTask");
        DelegateExpressions.registerJavaDelegateMock("battleUpdateWaitingProgressMessage");
        DelegateExpressions.registerJavaDelegateMock("noopTask");
        DelegateExpressions.registerJavaDelegateMock("processGameResultTask");
        DelegateExpressions.registerJavaDelegateMock("closeAssaultTask");
        DelegateExpressions.registerJavaDelegateMock("planetConqueredMessage");
        DelegateExpressions.registerJavaDelegateMock("planetDefendedMessage");
    }

    @After
    public void tearDown() {
        Mocks.reset();
    }

    @Test
    @Deployment(resources = "bpmn/planetary_assault.bpmn")
    public void success() throws Exception {
        DelegateExpressions.registerJavaDelegateMock("addCharacterToAssaultTask").onExecutionSetVariables(
                Variables.createVariables()
                        .putValue("gameFull", true)
        );
        DelegateExpressions.registerJavaDelegateMock("processGameResultTask").onExecutionSetVariables(
                Variables.createVariables()
                        .putValue("winner", "attacker")
        );

        final ProcessInstance processInstance = startProcess();

        processEngineRule.getRuntimeService().correlateMessage("Message_PlayerJoinsAssault", "test");
        processEngineRule.getRuntimeService().correlateMessage("Message_GameResult", "test",
                Variables.createVariables()
                        .putValue("gameResult", mock(GameResult.class))
        );


//        // Example for completing manual tasks:
//        TaskService taskService = processEngineRule.getTaskService();
//        assertThat(processInstance).isWaitingAtExactly("Task_110bnz1");
//        Task task = taskService.createTaskQuery().taskDefinitionKey("Task_110bnz1").singleResult();
//        taskService.complete(task.getId());

        verifyJavaDelegateMock("initiateAssaultTask").executed(times(1));
        verifyJavaDelegateMock("planetUnderAssaultMessage").executed(times(1));
        verifyJavaDelegateMock("userErrorMessage").executedNever();
        verifyJavaDelegateMock("addCharacterToAssaultTask").executed(times(1));
        verifyJavaDelegateMock("battleParticipantJoinedAssaultMessage").executed(times(1));
//        verifyJavaDelegateMock("createGameOptionsTask").executed(times(1));
        verifyJavaDelegateMock("processGameResultTask").executed(times(1));
        verifyJavaDelegateMock("closeAssaultTask").executed(times(1));
        verifyJavaDelegateMock("planetConqueredMessage").executed(times(1));

        assertThat(processInstance).isEnded();
    }

    @Test
    @Deployment(resources = "bpmn/planetary_assault.bpmn")
    public void errorInvalidCharacterActionOnInitiateAssault() {
        DelegateExpressions.registerJavaDelegateMock("initiateAssaultTask").onExecutionThrowBpmnError("2002", "Character not free for game");

        final ProcessInstance processInstance = startProcess();

        verifyJavaDelegateMock("initiateAssaultTask").executed(times(1));
        verifyJavaDelegateMock("planetUnderAssaultMessage").executedNever();
        verifyJavaDelegateMock("userAckMessage").executedNever();
        verifyJavaDelegateMock("userErrorMessage").executed(times(1));

        assertThat(processInstance).isEnded();
    }

    @Test
    @Deployment(resources = "bpmn/planetary_assault.bpmn")
    public void errorInvalidCharacterActionOnJoinAssault() {
        DelegateExpressions.registerJavaDelegateMock("addCharacterToAssaultTask").onExecutionThrowBpmnError("2002", "Character not free for game");

        final ProcessInstance processInstance = startProcess();
        verifyJavaDelegateMock("initiateAssaultTask").executed(times(1));
        verifyJavaDelegateMock("planetUnderAssaultMessage").executed(times(1));;
        verifyJavaDelegateMock("userAckMessage").executed(times(1));

        processEngineRule.getRuntimeService().correlateMessage("Message_PlayerJoinsAssault", "test");
        verifyJavaDelegateMock("addCharacterToAssaultTask").executed(times(1));;
        verifyJavaDelegateMock("userAckMessage").executed(times(1));
        verifyJavaDelegateMock("userErrorMessage").executed(times(1));
        verifyJavaDelegateMock("battleParticipantJoinedAssaultMessage").executedNever();

        assertThat(processInstance).isActive();
    }

    @Test
    @Deployment(resources = "bpmn/planetary_assault.bpmn")
    public void errorInvalidCharacterActionOnLeaveAssault() {
        DelegateExpressions.registerJavaDelegateMock("removeCharacterFromAssaultTask").onExecutionThrowBpmnError("2002", "Character not free for game");

        final ProcessInstance processInstance = startProcess();
        verifyJavaDelegateMock("initiateAssaultTask").executed(times(1));
        verifyJavaDelegateMock("planetUnderAssaultMessage").executed(times(1));;
        verifyJavaDelegateMock("userAckMessage").executed(times(1));

        processEngineRule.getRuntimeService().correlateMessage("Message_PlayerLeavesAssault", "test");
        verifyJavaDelegateMock("removeCharacterFromAssaultTask").executed(times(1));;
        verifyJavaDelegateMock("userAckMessage").executed(times(1));
        verifyJavaDelegateMock("userErrorMessage").executed(times(1));
        verifyJavaDelegateMock("battleParticipantLeftAssaultMessage").executedNever();

        assertThat(processInstance).isActive();
    }

    @Test
    @Deployment(resources = "bpmn/planetary_assault.bpmn")
    public void oneGameUpdateThenOnlyAttackerLeaves() {
        DelegateExpressions.registerJavaDelegateMock("removeCharacterFromAssaultTask").onExecutionSetVariables(
                Variables.createVariables().putValue("attackerCount", 0)
        );

        final ProcessInstance processInstance = startProcess();
        verifyJavaDelegateMock("initiateAssaultTask").executed(times(1));
        verifyJavaDelegateMock("planetUnderAssaultMessage").executed(times(1));
        verifyJavaDelegateMock("userAckMessage").executed(times(1));

        processEngineRule.getRuntimeService().signalEventReceived("Signal_UpdateOpenGames");
        verifyJavaDelegateMock("calculateWaitingProgressTask").executed(times(1));

        processEngineRule.getRuntimeService().correlateMessage("Message_PlayerLeavesAssault", "test");
        verifyJavaDelegateMock("userAckMessage").executed(times(2));
        verifyJavaDelegateMock("addCharacterToAssaultTask").executedNever();
        verifyJavaDelegateMock("battleUpdateWaitingProgressMessage").executed(times(1));
        verifyJavaDelegateMock("removeCharacterFromAssaultTask").executed(times(1));
        verifyJavaDelegateMock("battleParticipantLeftAssaultMessage").executed(times(1));
        verifyJavaDelegateMock("closeAssaultTask").executed(times(1));
        verifyJavaDelegateMock("planetDefendedMessage").executed(times(1));
        verifyJavaDelegateMock("processGameResultTask").executedNever();
        verifyJavaDelegateMock("userErrorMessage").executedNever();

        assertThat(processInstance).isEnded();
    }


    @Test
    @Deployment(resources = "bpmn/planetary_assault.bpmn")
    public void secondAttackerJoinsAndTimerRunsOut() throws Exception {
        DelegateExpressions.registerJavaDelegateMock("calculateWaitingProgressTask").onExecutionSetVariables(
                Variables.createVariables().putValue("waitingProgress", 1.0d)
        );

        org.h2.tools.Server.createWebServer("-web").start();
        final ProcessInstance processInstance = startProcess();
        verifyJavaDelegateMock("userAckMessage").executed(times(1));

        processEngineRule.getRuntimeService().correlateMessage("Message_PlayerJoinsAssault", "test");
        verifyJavaDelegateMock("initiateAssaultTask").executed(times(1));
        verifyJavaDelegateMock("planetUnderAssaultMessage").executed(times(1));
        verifyJavaDelegateMock("addCharacterToAssaultTask").executed(times(1));
        verifyJavaDelegateMock("battleParticipantJoinedAssaultMessage").executed(times(1));
        verifyJavaDelegateMock("userAckMessage").executed(times(2));


        processEngineRule.getRuntimeService().signalEventReceived("Signal_UpdateOpenGames");
        verifyJavaDelegateMock("calculateWaitingProgressTask").executed(times(1));
        verifyJavaDelegateMock("battleUpdateWaitingProgressMessage").executed(times(1));

        verifyJavaDelegateMock("removeCharacterFromAssaultTask").executedNever();
        verifyJavaDelegateMock("battleParticipantLeftAssaultMessage").executedNever();
        verifyJavaDelegateMock("closeAssaultTask").executed(times(1));
        verifyJavaDelegateMock("processGameResultTask").executedNever();
        verifyJavaDelegateMock("planetDefendedMessage").executedNever();
        verifyJavaDelegateMock("userErrorMessage").executedNever();

        assertThat(processInstance).isEnded();
    }

    private ProcessInstance startProcess() {
        Map<String, Object> processVariables = Variables.createVariables()
                .putValue("initiator", UUID.randomUUID())
                .putValue("planet", UUID.randomUUID())
                .putValue("gameFull", false)
                .putValue("waitingProgress", 0.0d)
                .putValue("attackerCount", 1)
                .putValue("defenderCount", 0);

        return processEngineRule.getRuntimeService().startProcessInstanceByMessage(
                PlanetaryAssaultService.INITIATE_ASSAULT_MESSAGE, "test", processVariables
        );
    }
}
