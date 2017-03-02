package com.faforever.gw.bpmn.task.regular_income;


import com.faforever.gw.bpmn.accessors.RegularIncomeAccessor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GiveRegularIncomeTask implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.trace("giveRegularIncomeTask");

        RegularIncomeAccessor accessor = RegularIncomeAccessor.of(execution);

        // TODO: Implement
        Long creditsTotal = 100L;
        Long creditsDelta = 10L;

        accessor.setCreditsTotal(100L)
                .setCreditsDelta(10L);
    }
}
