package com.faforever.gw.bpmn.task.regular_income;


import com.faforever.gw.bpmn.accessors.RegularIncomeAccessor;
import com.faforever.gw.config.GwServerProperties;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.services.ReinforcementsService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Slf4j
@Component
public class GiveRegularIncomeTask implements JavaDelegate {

    private final ReinforcementsService reinforcementsService;
    private final CharacterRepository characterRepository;
    private final GwServerProperties.Game gameProperties;

    @Inject
    public GiveRegularIncomeTask(ReinforcementsService reinforcementsService, CharacterRepository characterRepository, GwServerProperties gwServerProperties) {
        this.reinforcementsService = reinforcementsService;
        this.characterRepository = characterRepository;
        this.gameProperties = gwServerProperties.getGame();
    }

    @Override
    @Transactional(dontRollbackOn = BpmnError.class)
    public void execute(DelegateExecution execution) throws Exception {
        log.trace("giveRegularIncomeTask");

        RegularIncomeAccessor accessor = RegularIncomeAccessor.of(execution);

        GwCharacter character = characterRepository.findOne(accessor.getCharacter_Local());
        reinforcementsService.addIncome(character, gameProperties.getRegularIncomeCredits());
        Long creditsTotal = (long) reinforcementsService.getAvailableCredits(character);

        accessor.setCreditsTotal(creditsTotal);
        accessor.setCreditsDelta((long) gameProperties.getRegularIncomeCredits());
    }
}
