package com.faforever.gw.services;

import com.faforever.gw.bpmn.services.RegularIncomeService;
import org.camunda.bpm.engine.RuntimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RegularIncomeServiceTest {
    @Mock
    private RuntimeService runtimeService;

    private RegularIncomeService service;

    @BeforeEach
    public void setUp() throws Exception {
        service = new RegularIncomeService(runtimeService);
    }

    @Test
    public void testGenerateRegularIncome() throws Exception {
        service.generateRegularIncome();
        verify(runtimeService).signalEventReceived(RegularIncomeService.REGULAR_INCOME_DUE_SIGNAL);
    }
}
