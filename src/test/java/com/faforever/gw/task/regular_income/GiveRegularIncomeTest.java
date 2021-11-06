package com.faforever.gw.task.regular_income;

import com.faforever.gw.bpmn.task.regular_income.GiveRegularIncomeTask;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GiveRegularIncomeTest {
    private GiveRegularIncomeTask task;
    @Mock
    private DelegateExecution delegateExecution;

    @BeforeEach
    public void setUp() throws Exception {
        task = new GiveRegularIncomeTask();
    }

    @Test
    public void dummy() throws Exception {
        task.execute(delegateExecution);
    }
    // TODO: Add tests according to implementation

}