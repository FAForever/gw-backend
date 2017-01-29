package com.faforever.gw.bpmn.task;

import com.faforever.gw.model.*;
import com.faforever.gw.model.repository.BattleRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.UUID;

@Slf4j
@Component
public class AddCharacterToAssaultTask implements JavaDelegate {
    private final BattleRepository battleRepository;
    private final ValidationHelper validationHelper;

    @Inject
    public AddCharacterToAssaultTask(BattleRepository battleRepository, ValidationHelper validationHelper) {
        this.battleRepository = battleRepository;
        this.validationHelper = validationHelper;
    }

    @Override
    @Transactional
    public void execute(DelegateExecution execution) {
        log.debug("addCharacterToAssaultTask");

        Battle battle = battleRepository.getOne(UUID.fromString(execution.getProcessInstance().getBusinessKey()));
        GwCharacter character = (GwCharacter) execution.getVariable("character");
        Planet planet = (Planet)execution.getVariable("planet");

        validationHelper.validateCharacterInBattle(character, battle, false);
        validationHelper.validateCharacterFreeForGame(character);

        BattleRole battleRole;
        if (character.getFaction() == battle.getAttackingFaction()) {
            battleRole = BattleRole.ATTACKER;
        } else if (character.getFaction() == battle.getDefendingFaction()) {
            battleRole = BattleRole.DEFENDER;
        } else {
            battleRole = null;
        }

        validationHelper.validateOpenSlotForCharacter(character, battle, battleRole);

        BattleParticipant battleParticipant = new BattleParticipant(battle, character, battleRole);
        battle.getParticipants().add(battleParticipant);
        battleRepository.save(battle);

        execution.setVariable("gameFull", battle.getParticipants().size() == planet.getMap().getTotalSlots());
    }
}
