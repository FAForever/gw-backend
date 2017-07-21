package com.faforever.gw.bpmn;

import com.faforever.gw.bpmn.services.PlanetaryAssaultService;
import com.faforever.gw.model.GameResult;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.extension.mockito.DelegateExpressions;
import org.h2.tools.Server;
import org.junit.*;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.assertThat;
import static org.camunda.bpm.extension.mockito.DelegateExpressions.verifyJavaDelegateMock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;


public class PlanetaryAssaultTest {

    @Rule
    public ProcessEngineRule processEngineRule = new ProcessEngineRule();

    private static Server webServer;

    @BeforeClass
    public static void setUpClass() throws Exception {
        webServer = org.h2.tools.Server.createWebServer("-web");
        webServer.start();
    }

    @AfterClass
    public static void tearDownClass() {
        webServer.stop();
    }

    @Before
    public void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);
        DelegateExpressions.registerJavaDelegateMock("initiateAssaultTask").onExecutionSetVariables(
                Variables.createVariables()
                        .putValue("battle", UUID.randomUUID())
        );
        DelegateExpressions.registerJavaDelegateMock("planetUnderAssaultNotification");
        DelegateExpressions.registerJavaDelegateMock("userAckMessage");
        DelegateExpressions.registerJavaDelegateMock("userErrorMessage");
        DelegateExpressions.registerJavaDelegateMock("addCharacterToAssaultTask");
        DelegateExpressions.registerJavaDelegateMock("battleParticipantJoinedAssaultNotification");
        DelegateExpressions.registerJavaDelegateMock("removeCharacterFromAssaultTask");
        DelegateExpressions.registerJavaDelegateMock("battleParticipantLeftAssaultNotification");
        DelegateExpressions.registerJavaDelegateMock("calculateWaitingProgressTask");
        DelegateExpressions.registerJavaDelegateMock("battleUpdateWaitingProgressNotification");
        DelegateExpressions.registerJavaDelegateMock("noopTask");
        DelegateExpressions.registerJavaDelegateMock("processGameResultTask");
        DelegateExpressions.registerJavaDelegateMock("closeAssaultTask");
        DelegateExpressions.registerJavaDelegateMock("planetConqueredNotification");
        DelegateExpressions.registerJavaDelegateMock("planetDefendedNotification");
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
        verifyJavaDelegateMock("planetUnderAssaultNotification").executed(times(1));
        verifyJavaDelegateMock("userErrorMessage").executedNever();
        verifyJavaDelegateMock("addCharacterToAssaultTask").executed(times(1));
        verifyJavaDelegateMock("battleParticipantJoinedAssaultNotification").executed(times(1));
//        verifyJavaDelegateMock("createGameOptionsTask").executed(times(1));
        verifyJavaDelegateMock("processGameResultTask").executed(times(1));
        verifyJavaDelegateMock("closeAssaultTask").executed(times(1));
        verifyJavaDelegateMock("planetConqueredNotification").executed(times(1));

        assertThat(processInstance).isEnded();
    }

    @Test
    @Deployment(resources = "bpmn/planetary_assault.bpmn")
    public void errorInvalidCharacterActionOnInitiateAssault() {
        DelegateExpressions.registerJavaDelegateMock("initiateAssaultTask").onExecutionThrowBpmnError("2002", "Character not free for game");

        final ProcessInstance processInstance = startProcess();

        verifyJavaDelegateMock("initiateAssaultTask").executed(times(1));
        verifyJavaDelegateMock("planetUnderAssaultNotification").executedNever();
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
        verifyJavaDelegateMock("planetUnderAssaultNotification").executed(times(1));
        verifyJavaDelegateMock("userAckMessage").executed(times(1));

        processEngineRule.getRuntimeService().correlateMessage("Message_PlayerJoinsAssault", "test");
        verifyJavaDelegateMock("addCharacterToAssaultTask").executed(times(1));;
        verifyJavaDelegateMock("userAckMessage").executed(times(1));
        verifyJavaDelegateMock("userErrorMessage").executed(times(1));
        verifyJavaDelegateMock("battleParticipantJoinedAssaultNotification").executedNever();

        assertThat(processInstance).isActive();
    }

    @Test
    @Deployment(resources = "bpmn/planetary_assault.bpmn")
    public void errorInvalidCharacterActionOnLeaveAssault() {
        DelegateExpressions.registerJavaDelegateMock("removeCharacterFromAssaultTask").onExecutionThrowBpmnError("2002", "Character not free for game");

        final ProcessInstance processInstance = startProcess();
        verifyJavaDelegateMock("initiateAssaultTask").executed(times(1));
        verifyJavaDelegateMock("planetUnderAssaultNotification").executed(times(1));
        ;
        verifyJavaDelegateMock("userAckMessage").executed(times(1));

        processEngineRule.getRuntimeService().correlateMessage("Message_PlayerLeavesAssault", "test");
        verifyJavaDelegateMock("removeCharacterFromAssaultTask").executed(times(1));;
        verifyJavaDelegateMock("userAckMessage").executed(times(1));
        verifyJavaDelegateMock("userErrorMessage").executed(times(1));
        verifyJavaDelegateMock("battleParticipantLeftAssaultNotification").executedNever();

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
        verifyJavaDelegateMock("planetUnderAssaultNotification").executed(times(1));
        verifyJavaDelegateMock("userAckMessage").executed(times(1));

        processEngineRule.getRuntimeService().signalEventReceived("Signal_UpdateOpenGames");
        verifyJavaDelegateMock("calculateWaitingProgressTask").executed(times(1));

        processEngineRule.getRuntimeService().correlateMessage("Message_PlayerLeavesAssault", "test");
        verifyJavaDelegateMock("userAckMessage").executed(times(2));
        verifyJavaDelegateMock("addCharacterToAssaultTask").executedNever();
        verifyJavaDelegateMock("battleUpdateWaitingProgressNotification").executed(times(1));
        verifyJavaDelegateMock("removeCharacterFromAssaultTask").executed(times(1));
        verifyJavaDelegateMock("battleParticipantLeftAssaultNotification").executed(times(1));
        verifyJavaDelegateMock("closeAssaultTask").executed(times(1));
        verifyJavaDelegateMock("planetDefendedNotification").executed(times(1));
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

        final ProcessInstance processInstance = startProcess();
        verifyJavaDelegateMock("userAckMessage").executed(times(1));

        processEngineRule.getRuntimeService().correlateMessage("Message_PlayerJoinsAssault", "test");
        verifyJavaDelegateMock("initiateAssaultTask").executed(times(1));
        verifyJavaDelegateMock("planetUnderAssaultNotification").executed(times(1));
        verifyJavaDelegateMock("addCharacterToAssaultTask").executed(times(1));
        verifyJavaDelegateMock("battleParticipantJoinedAssaultNotification").executed(times(1));
        verifyJavaDelegateMock("userAckMessage").executed(times(2));


        processEngineRule.getRuntimeService().signalEventReceived("Signal_UpdateOpenGames");
        verifyJavaDelegateMock("calculateWaitingProgressTask").executed(times(1));
        verifyJavaDelegateMock("battleUpdateWaitingProgressNotification").executed(times(1));

        verifyJavaDelegateMock("removeCharacterFromAssaultTask").executedNever();
        verifyJavaDelegateMock("battleParticipantLeftAssaultNotification").executedNever();
        verifyJavaDelegateMock("closeAssaultTask").executed(times(1));
        verifyJavaDelegateMock("processGameResultTask").executedNever();
        verifyJavaDelegateMock("planetDefendedNotification").executedNever();
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
