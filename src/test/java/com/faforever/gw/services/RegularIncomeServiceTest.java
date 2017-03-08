package com.faforever.gw.services;

import com.faforever.gw.bpmn.services.RegularIncomeService;
import org.camunda.bpm.engine.RuntimeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RegularIncomeServiceTest {
    @Mock
    private RuntimeService runtimeService;

    private RegularIncomeService service;

    @Before
    public void setUp() throws Exception {
        service = new RegularIncomeService(runtimeService);
    }

    @Test
    public void testGenerateRegularIncome() throws Exception {
        service.generateRegularIncome();
        verify(runtimeService).signalEventReceived(RegularIncomeService.REGULAR_INCOME_DUE_SIGNAL);
    }
}
