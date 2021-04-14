package com.faforever.gw.bpmn;

import com.faforever.gw.model.GwCharacter;
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

import java.util.Arrays;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.mockito.Mockito.mock;

public class CalculatePromotionsTest {

    @Rule
    public ProcessEngineRule processEngineRule = new ProcessEngineRule();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DelegateExpressions.registerJavaDelegateMock("selectAllActiveCharactersTask").onExecutionSetVariables(ImmutableMap.of("activeCharacters", Arrays.asList(mock(GwCharacter.class), mock(GwCharacter.class))));
        DelegateExpressions.registerJavaDelegateMock("checkAndPerformPromotionTask");
        DelegateExpressions.registerJavaDelegateMock("characterPromotionNotification");
    }

    @After
    public void tearDown() {
        Mocks.reset();
    }

    @Test
    @Deployment(resources = "bpmn/calculate_promotions.bpmn")
    public void success_2active_bothPromoted() throws Exception {
        DelegateExpressions.registerJavaDelegateMock("checkAndPerformPromotionTask").onExecutionSetVariables(ImmutableMap.of("rankAvailable", true));

        ProcessInstance processInstance = startProcess();

        assertThat(processInstance).hasPassedInOrder(
                "StartEvent_UpdatePromotions",
                "ServiceTask_SelectAllActiveCharacters",
                "StartEvent_CharacterPromotion",
                "Task_GetAvailableRank",
                "ExclusiveGateway_NewRankAvailable",
                "EndEvent_CharacterPromoted",
                "StartEvent_CharacterPromotion",
                "Task_GetAvailableRank",
                "ExclusiveGateway_NewRankAvailable",
                "EndEvent_CharacterPromoted",
                "EndEvent_PromotionsUpdated"
        );
    }

    @Test
    @Deployment(resources = "bpmn/calculate_promotions.bpmn")
    public void success_2active_nonePromoted() throws Exception {
        DelegateExpressions.registerJavaDelegateMock("checkAndPerformPromotionTask").onExecutionSetVariables(ImmutableMap.of("rankAvailable", false));

        ProcessInstance processInstance = startProcess();

        assertThat(processInstance).hasPassedInOrder(
                "StartEvent_UpdatePromotions",
                "ServiceTask_SelectAllActiveCharacters",
                "StartEvent_CharacterPromotion",
                "Task_GetAvailableRank",
                "ExclusiveGateway_NewRankAvailable",
                "EndEvent_CharacterNotPromoted",
                "StartEvent_CharacterPromotion",
                "Task_GetAvailableRank",
                "ExclusiveGateway_NewRankAvailable",
                "EndEvent_CharacterNotPromoted",
                "EndEvent_PromotionsUpdated"
        );
    }

    private ProcessInstance startProcess() {
        return processEngineRule.getRuntimeService().startProcessInstanceByKey("Process_CalculatePromotions");
//        processEngineRule.getRuntimeService().signalEventReceived("Signal_UpdatePromotions");
    }
}
