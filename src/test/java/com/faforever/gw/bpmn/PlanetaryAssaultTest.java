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
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.execute;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.job;
import static org.mockito.Mockito.mock;


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
        DelegateExpressions.registerJavaDelegateMock("setupLobbyMatchTask");
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

        assertThat(processInstance).hasPassedInOrder(
                "StartEvent_CharacterInitiatesAssault",
                "ServiceTask_InitateAssault");

        execute(job()); // IntermediateThrowEvent_PlanetUnderAssault is async

        assertThat(processInstance).hasPassedInOrder(
                "ServiceTask_InitateAssault",
                "IntermediateThrowEvent_PlanetUnderAssault",
                "IntermediateThrowEvent_JoinedAssault_FirstPlayer",
                "StartEvent_AssaultInitiated");

        assertThat(processInstance).isWaitingAt("ExclusiveGateway_SetupMatch");

        processEngineRule.getRuntimeService().correlateMessage("Message_PlayerJoinsAssault", "test");

        assertThat(processInstance).hasPassedInOrder(
                "StartEvent_AssaultInitiated",
                "ExclusiveGateway_SetupMatch",
                "IntermediateCatchEvent_PlayerJoins",
                "ServiceTask_AddPlayersCharacterToAssault");

        execute(job()); // IntermediateThrowEvent_JoinedAssault is async

        assertThat(processInstance).hasPassedInOrder(
                "ServiceTask_AddPlayersCharacterToAssault",
                "IntermediateThrowEvent_JoinedAssault",
                "ExclusiveGateway_AllSlotsOccupied",
                "ExclusiveGateway_MergeMatchStarting",
                "EndEvent_MatchStarting",
                "Task_SetupMatch",
                "ServiceTask_CreateGameOptions",
                "ServiceTask_ConsumePlayersReinforcements",
                "ServiceTask_CommandLobbyServerToSetupMatch");

        processEngineRule.getRuntimeService().correlateMessage("Message_GameResult", "test",
                Variables.createVariables()
                        .putValue("gameResult", mock(GameResult.class))
        );

        assertThat(processInstance).hasPassedInOrder(
                "ServiceTask_CommandLobbyServerToSetupMatch",
                "IntermediateCatchEvent_GameResult",
                "ServiceTask_ProcessGameResults",
                "IntermediateThrowEvent_UpdatePromotions",
                "ExclusiveGateway_MergeAssaultFinish",
                "ServiceTask_CloseAssault",
                "ExclusiveGateway_AttackersWin",
                "EndEvent_PlanetConquered");

        assertThat(processInstance).isEnded();
    }

    @Test
    @Deployment(resources = "bpmn/planetary_assault.bpmn")
    public void errorInvalidCharacterActionOnInitiateAssault() {
        DelegateExpressions.registerJavaDelegateMock("initiateAssaultTask").onExecutionThrowBpmnError("2002", "Character not free for game");

        final ProcessInstance processInstance = startProcess();

        assertThat(processInstance).hasPassedInOrder(
                "StartEvent_CharacterInitiatesAssault",
                "ServiceTask_InitateAssault",
                "EndEvent_Error_InvalidCharacterAction");

        assertThat(processInstance).isEnded();
    }

    @Test
    @Deployment(resources = "bpmn/planetary_assault.bpmn")
    public void errorInvalidCharacterActionOnJoinAssault() {
        DelegateExpressions.registerJavaDelegateMock("addCharacterToAssaultTask").onExecutionThrowBpmnError("2002", "Character not free for game");

        final ProcessInstance processInstance = startProcess();

        assertThat(processInstance).hasPassedInOrder(
                "StartEvent_CharacterInitiatesAssault",
                "ServiceTask_InitateAssault");

        execute(job()); // IntermediateThrowEvent_PlanetUnderAssault is async

        assertThat(processInstance).hasPassedInOrder(
                "ServiceTask_InitateAssault",
                "IntermediateThrowEvent_PlanetUnderAssault",
                "IntermediateThrowEvent_JoinedAssault_FirstPlayer",
                "StartEvent_AssaultInitiated");

        assertThat(processInstance).isWaitingAt("ExclusiveGateway_SetupMatch");

        processEngineRule.getRuntimeService().correlateMessage("Message_PlayerJoinsAssault", "test");

        assertThat(processInstance).hasPassedInOrder(
                "StartEvent_AssaultInitiated",
                "ExclusiveGateway_SetupMatch",
                "IntermediateCatchEvent_PlayerJoins",
                "ServiceTask_AddPlayersCharacterToAssault",
                "BoundaryEvent_Error_AddCharacter",
                "IntermediateThrowEvent_Error_AddCharacter");

        assertThat(processInstance).isWaitingAt("ExclusiveGateway_SetupMatch");
    }

    @Test
    @Deployment(resources = "bpmn/planetary_assault.bpmn")
    public void errorInvalidCharacterActionOnLeaveAssault() {
        DelegateExpressions.registerJavaDelegateMock("removeCharacterFromAssaultTask").onExecutionThrowBpmnError("2002", "Character not free for game");

        final ProcessInstance processInstance = startProcess();

        assertThat(processInstance).hasPassedInOrder(
                "StartEvent_CharacterInitiatesAssault",
                "ServiceTask_InitateAssault");

        execute(job()); // IntermediateThrowEvent_PlanetUnderAssault is async

        assertThat(processInstance).hasPassedInOrder(
                "ServiceTask_InitateAssault",
                "IntermediateThrowEvent_PlanetUnderAssault",
                "IntermediateThrowEvent_JoinedAssault_FirstPlayer",
                "StartEvent_AssaultInitiated");

        assertThat(processInstance).isWaitingAt("ExclusiveGateway_SetupMatch");

        processEngineRule.getRuntimeService().correlateMessage("Message_PlayerLeavesAssault", "test");

        assertThat(processInstance).hasPassedInOrder(
                "StartEvent_AssaultInitiated",
                "ExclusiveGateway_SetupMatch",
                "IntermediateCatchEvent_PlayerLeaves",
                "ServiceTask_RemovePlayersCharacterToAssault",
                "BoundaryEvent_Error_RemoveCharacter",
                "IntermediateThrowEvent_Error_RemoveCharacter");

        assertThat(processInstance).isWaitingAt("ExclusiveGateway_SetupMatch");
    }

    @Test
    @Deployment(resources = "bpmn/planetary_assault.bpmn")
    public void oneGameUpdateThenOnlyAttackerLeaves() {
        DelegateExpressions.registerJavaDelegateMock("removeCharacterFromAssaultTask").onExecutionSetVariables(
                Variables.createVariables().putValue("attackerCount", 0)
        );

        final ProcessInstance processInstance = startProcess();

        assertThat(processInstance).hasPassedInOrder(
                "StartEvent_CharacterInitiatesAssault",
                "ServiceTask_InitateAssault");

        execute(job()); // IntermediateThrowEvent_PlanetUnderAssault is async

        assertThat(processInstance).hasPassedInOrder(
                "ServiceTask_InitateAssault",
                "IntermediateThrowEvent_PlanetUnderAssault",
                "IntermediateThrowEvent_JoinedAssault_FirstPlayer",
                "StartEvent_AssaultInitiated");

        assertThat(processInstance).isWaitingAt("ExclusiveGateway_SetupMatch");

        processEngineRule.getRuntimeService().signalEventReceived("Signal_UpdateOpenGames");

        assertThat(processInstance).hasPassedInOrder(
                "StartEvent_AssaultInitiated",
                "ExclusiveGateway_SetupMatch",
                "IntermediateCatchEvent_UpdateOpenGames",
                "ServiceTask_CalculateWaitingProgress");

        execute(job()); // IntermediateThrowEvent_UpdateWaitingProgress is async

        assertThat(processInstance).hasPassedInOrder(
                "ServiceTask_CalculateWaitingProgress",
                "IntermediateThrowEvent_UpdateWaitingProgress",
                "ExclusiveGateway_WaitingProgress");

        assertThat(processInstance).isWaitingAt("ExclusiveGateway_SetupMatch");

        processEngineRule.getRuntimeService().correlateMessage("Message_PlayerLeavesAssault", "test");

        assertThat(processInstance).hasPassedInOrder(
                "ExclusiveGateway_WaitingProgress",
                "IntermediateCatchEvent_PlayerLeaves",
                "ServiceTask_RemovePlayersCharacterToAssault");

        execute(job()); // IntermediateThrowEvent_LeftAssault is async

        assertThat(processInstance).hasPassedInOrder(
                "ServiceTask_RemovePlayersCharacterToAssault",
                "ExclusiveGateway_AssaultFactionHasPlayers",
                "ServiceTask_SetDefenderAsWinner");

        execute(job()); // EndEvent_DefendersWon_NoMatch is async

        assertThat(processInstance).hasPassedInOrder(
                "ServiceTask_SetDefenderAsWinner",
                "EndEvent_DefendersWon_NoMatch",
                "Task_SetupMatch",
                "BoundaryEvent_DefendersWon_NoMatch",
                "ExclusiveGateway_MergeAssaultFinish",
                "ServiceTask_CloseAssault",
                "ExclusiveGateway_AttackersWin",
                "EndEvent_PlanetDefended");

        assertThat(processInstance).isEnded();
    }


    @Test
    @Deployment(resources = "bpmn/planetary_assault.bpmn")
    public void secondAttackerJoinsAndTimerRunsOut() throws Exception {
        DelegateExpressions.registerJavaDelegateMock("calculateWaitingProgressTask").onExecutionSetVariables(
                Variables.createVariables().putValue("waitingProgress", 1.0d)
        );

        final ProcessInstance processInstance = startProcess();

        assertThat(processInstance).hasPassedInOrder(
                "StartEvent_CharacterInitiatesAssault",
                "ServiceTask_InitateAssault");

        execute(job()); // IntermediateThrowEvent_PlanetUnderAssault is async

        assertThat(processInstance).hasPassedInOrder(
                "ServiceTask_InitateAssault",
                "IntermediateThrowEvent_PlanetUnderAssault",
                "IntermediateThrowEvent_JoinedAssault_FirstPlayer",
                "StartEvent_AssaultInitiated");

        assertThat(processInstance).isWaitingAt("ExclusiveGateway_SetupMatch");

        processEngineRule.getRuntimeService().correlateMessage("Message_PlayerJoinsAssault", "test");

        assertThat(processInstance).hasPassedInOrder(
                "StartEvent_AssaultInitiated",
                "IntermediateCatchEvent_PlayerJoins",
                "ServiceTask_AddPlayersCharacterToAssault");

        execute(job()); // IntermediateThrowEvent_JoinedAssault is async

        assertThat(processInstance).hasPassedInOrder(
                "ServiceTask_AddPlayersCharacterToAssault",
                "ExclusiveGateway_AllSlotsOccupied");

        assertThat(processInstance).isWaitingAt("ExclusiveGateway_SetupMatch");

        processEngineRule.getRuntimeService().signalEventReceived("Signal_UpdateOpenGames");

        assertThat(processInstance).hasPassedInOrder(
                "ExclusiveGateway_SetupMatch",
                "IntermediateCatchEvent_UpdateOpenGames",
                "ServiceTask_CalculateWaitingProgress");

        execute(job()); // IntermediateThrowEvent_UpdateWaitingProgress is async

        assertThat(processInstance).hasPassedInOrder(
                "ServiceTask_CalculateWaitingProgress",
                "IntermediateThrowEvent_UpdateWaitingProgress",
                "ExclusiveGateway_WaitingProgress",
                "ExclusiveGateway_MergeAttackersWin",
                "ServiceTask_SetAtackerAsWinner");


        execute(job()); // EndEvent_AttackersWon_NoMatch is async

        assertThat(processInstance).hasPassedInOrder(
                "ServiceTask_SetAtackerAsWinner",
                "EndEvent_AttackersWon_NoMatch",
                "Task_SetupMatch",
                "BoundaryEvent_AttackersWon_NoMatch",
                "ExclusiveGateway_MergeAssaultFinish",
                "ServiceTask_CloseAssault",
                "ExclusiveGateway_AttackersWin",
                "EndEvent_PlanetConquered");

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
