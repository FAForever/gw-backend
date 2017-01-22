package com.faforever.gw.bpmn;

import com.faforever.gw.bpmn.accessor.InitiateAssaultEventMessage;
import com.faforever.gw.bpmn.task.InitiateAssaultTask;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Planet;
import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.mockito.DelegateExpressions;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
        DelegateExpressions.registerJavaDelegateMock("initiateAssaultTask");
        DelegateExpressions.registerJavaDelegateMock("assaultInitiatedSuccessMessage");
        DelegateExpressions.registerJavaDelegateMock("playerNotFreeForGameErrorMessage");
        DelegateExpressions.registerJavaDelegateMock("planetNotAttackableErrorMessage");
        DelegateExpressions.registerJavaDelegateMock("addCharacterToAssaultTask");
        DelegateExpressions.registerJavaDelegateMock("createGameOptionsTask");
        DelegateExpressions.registerJavaDelegateMock("noopTask");
    }

    @After
    public void tearDown() {
        Mocks.reset();
    }

    @Test
    @Deployment(resources = "bpmn/planetary_assault.bpmn")
    public void success() throws Exception {
        DelegateExpressions.registerJavaDelegateMock("initiateAssaultTask").onExecutionSetVariables(ImmutableMap.of("battle", mock(Battle.class)));
        DelegateExpressions.registerJavaDelegateMock("addCharacterToAssaultTask").onExecutionSetVariables(ImmutableMap.of("gameFull", true));

//        // only use if necessary - connect via http://localhost:8082
//        org.h2.tools.Server.createWebServer("-web").start();

        final ProcessInstance processInstance = startProcess();

        processEngineRule.getRuntimeService().correlateMessage("Message_PlayerJoinsAssault", "test");
        processEngineRule.getRuntimeService().correlateMessage("Message_GameResult", "test", ImmutableMap.of("characterResults", new ArrayList<String>(), "winner", "attackers"));

//        // Example for completing manual tasks:
//        TaskService taskService = processEngineRule.getTaskService();
//        assertThat(processInstance).isWaitingAtExactly("Task_110bnz1");
//        Task task = taskService.createTaskQuery().taskDefinitionKey("Task_110bnz1").singleResult();
//        taskService.complete(task.getId());

        verifyJavaDelegateMock("initiateAssaultTask").executed(times(1));
        verifyJavaDelegateMock("assaultInitiatedSuccessMessage").executed(times(1));
        verifyJavaDelegateMock("addCharacterToAssaultTask").executed(times(1));
        verifyJavaDelegateMock("createGameOptionsTask").executed(times(1));
        assertThat(processInstance).isEnded();
    }

    @Test
    @Deployment(resources = "bpmn/planetary_assault.bpmn")
    public void errorInitiatorNotFreeForGame() {
        DelegateExpressions.registerJavaDelegateMock("initiateAssaultTask").onExecutionThrowBpmnError(new BpmnError("PlayerNotFreeForGame"));

        final ProcessInstance processInstance = startProcess();

        verifyJavaDelegateMock("initiateAssaultTask").executed(times(1));
        verifyJavaDelegateMock("assaultInitiatedSuccessMessage").executedNever();
        verifyJavaDelegateMock("playerNotFreeForGameErrorMessage").executed(times(1));
        verifyJavaDelegateMock("planetNotAttackableErrorMessage").executedNever();

        assertThat(processInstance).isEnded();
    }

    @Test
    @Deployment(resources = "bpmn/planetary_assault.bpmn")
    public void errorPlanetNotAttackable() {
        DelegateExpressions.registerJavaDelegateMock("initiateAssaultTask").onExecutionThrowBpmnError(new BpmnError("PlanetNotAttackable"));

        final ProcessInstance processInstance = startProcess();

        verifyJavaDelegateMock("initiateAssaultTask").executed(times(1));
        verifyJavaDelegateMock("assaultInitiatedSuccessMessage").executedNever();
        verifyJavaDelegateMock("playerNotFreeForGameErrorMessage").executedNever();
        verifyJavaDelegateMock("planetNotAttackableErrorMessage").executed(times(1));

        assertThat(processInstance).isEnded();
    }

    private ProcessInstance startProcess() {
        Map<String, Object> processVariables = ImmutableMap.of("initiator", mock(GwCharacter.class), "planet", mock(Planet.class));

        return processEngineRule.getRuntimeService().startProcessInstanceByMessage(
                InitiateAssaultEventMessage.MESSAGE_NAME, "test", processVariables
        );
    }
}
