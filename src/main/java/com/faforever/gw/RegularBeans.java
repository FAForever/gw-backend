package com.faforever.gw;

import com.faforever.gw.model.Planet;
import com.faforever.gw.model.repository.PlanetRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@Component
public class RegularBeans {
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final PlanetRepository planetRepository;

    @Inject
    public RegularBeans(RepositoryService repositoryService, RuntimeService runtimeService, PlanetRepository planetRepository) {
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.planetRepository = planetRepository;
    }

    public void generateRegularIncome() {
        List<Planet> planets = planetRepository.findAll();

        log.info("generate regular income (send signal)");
//        runtimeService.signalEventReceived("Signal_RegularIncomeDue");
    }


    public JavaDelegate giveRegularIncome() {
        return execution -> log.info("give income to "+execution.getVariable("character").toString());
    }

}
