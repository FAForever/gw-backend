package com.faforever.gw.bpmn;

import com.google.common.collect.ImmutableMap;
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

import static org.camunda.bpm.extension.mockito.DelegateExpressions.verifyJavaDelegateMock;
import static org.mockito.Mockito.times;


public class RegularIncomeTest {

    @Rule
    public ProcessEngineRule processEngineRule = new ProcessEngineRule();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DelegateExpressions.registerJavaDelegateMock("selectAllActiveCharactersTask").onExecutionSetVariables(ImmutableMap.of("activeCharacters", Arrays.asList("GwCharacter.class", "GwCharacter.class")));
        DelegateExpressions.registerJavaDelegateMock("giveRegularIncomeTask");
        DelegateExpressions.registerJavaDelegateMock("regularIncomeMessage");
    }

    @After
    public void tearDown() {
        Mocks.reset();
    }

    @Test
    @Deployment(resources = "bpmn/regular_income.bpmn")
    public void success() throws Exception {
        startProcess();

        verifyJavaDelegateMock("selectAllActiveCharactersTask").executed(times(1));
        verifyJavaDelegateMock("giveRegularIncomeTask").executed(times(2));
        verifyJavaDelegateMock("regularIncomeMessage").executed(times(2));
    }

    private void startProcess() {
        processEngineRule.getRuntimeService().signalEventReceived("Signal_RegularIncomeDue");
    }
}
