package com.faforever.gw.bpmn.task.planetary_assault;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.BattleRole;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.ValidationHelper;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
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
    @Transactional(dontRollbackOn = BpmnError.class)
    public void execute(DelegateExecution execution) {
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution);
        log.debug("removeCharacterFromAssaultTask for battle {}", accessor.getBusinessKey());

        Battle battle = battleRepository.getOne(accessor.getBattleId());
        GwCharacter character = characterRepository.getOne(accessor.getRequestCharacter());

        validationHelper.validateCharacterInBattle(character, battle, true);

        battle.getParticipants().removeIf(battleParticipant -> battleParticipant.getCharacter() == character);
        battleRepository.save(battle);

        Integer newParticipantsOfFactionCount = 0;
        Boolean noMoreAttackerRemaining = false;
        if (character.getFaction() == battle.getAttackingFaction()) {
            newParticipantsOfFactionCount = accessor.getAttackerCount() - 1;
            accessor.setParticipantCount(BattleRole.ATTACKER, newParticipantsOfFactionCount);
            noMoreAttackerRemaining = (newParticipantsOfFactionCount == 0);
        } else if (character.getFaction() == battle.getDefendingFaction()) {
            newParticipantsOfFactionCount = accessor.getDefenderCount() - 1;
            accessor.setParticipantCount(BattleRole.DEFENDER, newParticipantsOfFactionCount);
        }

        accessor.setGameFull(false);

        log.info("Character {} left battle {}", character.getId(), battle.getId());

        if (noMoreAttackerRemaining) {
            log.info("Battle {} won by defender (all attacker left)", battle.getId());
            accessor.setWinner(BattleRole.DEFENDER);
        }
    }
}
