package com.faforever.gw;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class Scheduler {
    @Autowired
    private RegularBeans regularBeans;

    @Scheduled(fixedDelay = 30000)
    private void generateRegularIncome() {
        regularBeans.generateRegularIncome();
    }
}
