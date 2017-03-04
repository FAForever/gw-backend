package com.faforever.gw.bpmn.services;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
/**
 * Service class for the BPMN process "regular income"
 */
public class RegularIncomeService {
    public static final String REGULAR_INCOME_DUE_SIGNAL = "Signal_RegularIncomeDue";

    private final RuntimeService runtimeService;

    public RegularIncomeService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @Scheduled(fixedDelay = 3600000)
    public void generateRegularIncome() {
        log.info("generate regular income");
        runtimeService.signalEventReceived(REGULAR_INCOME_DUE_SIGNAL);
    }
}
