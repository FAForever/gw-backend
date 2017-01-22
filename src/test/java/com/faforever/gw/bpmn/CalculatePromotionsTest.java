package com.faforever.gw.bpmn;

import com.faforever.gw.bpmn.task.InitiateAssaultTask;
import com.faforever.gw.model.GwCharacter;
import jersey.repackaged.com.google.common.collect.ImmutableMap;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;


public class CalculatePromotionsTest {

    @Rule
    public ProcessEngineRule processEngineRule = new ProcessEngineRule();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DelegateExpressions.registerJavaDelegateMock("selectAllActiveCharactersTask").onExecutionSetVariables(ImmutableMap.of("activeCharacters", Arrays.asList(mock(GwCharacter.class), mock(GwCharacter.class))));
        DelegateExpressions.registerJavaDelegateMock("getAvailableRankTask");
        DelegateExpressions.registerJavaDelegateMock("promoteCharacterTask");
    }

    @After
    public void tearDown() {
        Mocks.reset();
    }

    @Test
    @Deployment(resources = "bpmn/calculate_promotions.bpmn")
    public void success_2active_bothPromoted() throws Exception {
        DelegateExpressions.registerJavaDelegateMock("getAvailableRankTask").onExecutionSetVariables(ImmutableMap.of("rankAvailable", true));

        startProcess();

        verifyJavaDelegateMock("selectAllActiveCharactersTask").executed(times(1));
        verifyJavaDelegateMock("getAvailableRankTask").executed(times(2));
        verifyJavaDelegateMock("promoteCharacterTask").executed(times(2));
    }

    @Test
    @Deployment(resources = "bpmn/calculate_promotions.bpmn")
    public void success_2active_nonePromoted() throws Exception {
        DelegateExpressions.registerJavaDelegateMock("getAvailableRankTask").onExecutionSetVariables(ImmutableMap.of("rankAvailable", false));

        startProcess();

        verifyJavaDelegateMock("selectAllActiveCharactersTask").executed(times(1));
        verifyJavaDelegateMock("getAvailableRankTask").executed(times(2));
        verifyJavaDelegateMock("promoteCharacterTask").executed(times(0));
    }

    private void startProcess() {
        processEngineRule.getRuntimeService().signalEventReceived("Signal_UpdatePromotions");
    }
}
