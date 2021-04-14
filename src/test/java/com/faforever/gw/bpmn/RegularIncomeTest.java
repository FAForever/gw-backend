package com.faforever.gw.bpmn;

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


public class RegularIncomeTest {

    @Rule
    public ProcessEngineRule processEngineRule = new ProcessEngineRule();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DelegateExpressions.registerJavaDelegateMock("selectAllActiveCharactersTask").onExecutionSetVariables(ImmutableMap.of("activeCharacters", Arrays.asList("GwCharacter.class", "GwCharacter.class")));
        DelegateExpressions.registerJavaDelegateMock("giveRegularIncomeTask");
        DelegateExpressions.registerJavaDelegateMock("regularIncomeNotification");
    }

    @After
    public void tearDown() {
        Mocks.reset();
    }

    @Test
    @Deployment(resources = "bpmn/regular_income.bpmn")
    public void success() throws Exception {
        ProcessInstance processInstance = startProcess();

        assertThat(processInstance).hasPassedInOrder(
                "StartEvent_RegularIncomeDue",
                "ServiceTask_SelectAllActiveCharacters",
                "StartEvent_GenerateIncomeSub",
                "ServiceTask_GiveRegularIncome",
                "EndEvent_GenerateIncomeSub",
                "StartEvent_GenerateIncomeSub",
                "ServiceTask_GiveRegularIncome",
                "EndEvent_GenerateIncomeSub",
                "EndEvent_RegularIncomeGenerated"
        ).isEnded();

    }

    private ProcessInstance startProcess() {
        return processEngineRule.getRuntimeService().startProcessInstanceByKey("Process_RegularIncome");
//        processEngineRule.getRuntimeService().signalEventReceived("Signal_RegularIncomeDue");
    }
}
