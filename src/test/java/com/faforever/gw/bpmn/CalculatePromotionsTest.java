package com.faforever.gw.bpmn;

import com.faforever.gw.model.GwCharacter;
import com.google.common.collect.ImmutableMap;
import org.camunda.bpm.engine.ProcessEngine;
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

import java.util.Arrays;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(ProcessEngineExtension.class)
public class CalculatePromotionsTest {

    public ProcessEngine processEngine;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DelegateExpressions.registerJavaDelegateMock("selectAllActiveCharactersTask").onExecutionSetVariables(ImmutableMap.of("activeCharacters", Arrays.asList(mock(GwCharacter.class), mock(GwCharacter.class))));
        DelegateExpressions.registerJavaDelegateMock("checkAndPerformPromotionTask");
        DelegateExpressions.registerJavaDelegateMock("characterPromotionNotification");
    }

    @AfterEach
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
        return processEngine.getRuntimeService().startProcessInstanceByKey("Process_CalculatePromotions");
//        processEngine.getRuntimeService().signalEventReceived("Signal_UpdatePromotions");
    }
}
