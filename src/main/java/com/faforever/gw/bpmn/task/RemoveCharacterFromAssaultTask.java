package com.faforever.gw.bpmn.task;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.model.*;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Slf4j
@Component
public class RemoveCharacterFromAssaultTask implements JavaDelegate {
    private final CharacterRepository characterRepository;
    private final BattleRepository battleRepository;
    private final ValidationHelper validationHelper;

    @Inject
    public RemoveCharacterFromAssaultTask(CharacterRepository characterRepository, BattleRepository battleRepository, ValidationHelper validationHelper) {
        this.characterRepository = characterRepository;
        this.battleRepository = battleRepository;
        this.validationHelper = validationHelper;
    }

    @Override
    @Transactional
    public void execute(DelegateExecution execution) {
        log.debug("removeCharacterFromAssaultTask");

        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution.getVariables());

        Battle battle = battleRepository.getOne(accessor.getBattleId());
        GwCharacter character = characterRepository.getOne(accessor.getLastLeftCharacter());

        validationHelper.validateCharacterInBattle(character, battle, true);

        battle.getParticipants().removeIf(battleParticipant -> battleParticipant.getCharacter() == character);
        battleRepository.save(battle);

        long attackerCount = battle.getParticipants().stream()
                .filter(battleParticipant -> battleParticipant.getRole() == BattleRole.ATTACKER)
                .count();

        execution.setVariable("assaultFactionHasRemainingPlayers", attackerCount > 0);
        execution.setVariable("gameFull", false);

        if (attackerCount == 0) {
            execution.setVariable("winner", BattleRole.DEFENDER.getName());
        }
    }
}
