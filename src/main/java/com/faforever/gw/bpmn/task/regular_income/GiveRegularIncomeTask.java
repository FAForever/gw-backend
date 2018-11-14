package com.faforever.gw.bpmn.task.regular_income;


import com.faforever.gw.bpmn.accessors.RegularIncomeAccessor;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.service.CharacterService;
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

    private CharacterService characterService;
    private CharacterRepository characterRepository;

    //TODO: where to put game related config data? dmn?
    private static final double REGULAR_INCOME_AMOUNT = 100.0;

    @Inject
    public GiveRegularIncomeTask(CharacterService characterService, CharacterRepository characterRepository) {
        this.characterService = characterService;
        this.characterRepository = characterRepository;
    }

    @Override
    @Transactional(dontRollbackOn = BpmnError.class)
    public void execute(DelegateExecution execution) throws Exception {
        log.trace("giveRegularIncomeTask");

        RegularIncomeAccessor accessor = RegularIncomeAccessor.of(execution);

        GwCharacter character = characterRepository.findOne(accessor.getCharacter_Local());
        characterService.addIncome(character, REGULAR_INCOME_AMOUNT);
        Long creditsTotal = (long) characterService.getAvailableCredits(character);

        accessor.setCreditsTotal(creditsTotal);
        accessor.setCreditsDelta((long) REGULAR_INCOME_AMOUNT)
    }
}
