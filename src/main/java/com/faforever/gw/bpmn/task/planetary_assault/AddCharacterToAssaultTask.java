package com.faforever.gw.bpmn.task.planetary_assault;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.model.*;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddCharacterToAssaultTask implements JavaDelegate {
    private final CharacterRepository characterRepository;
    private final BattleRepository battleRepository;
    private final PlanetRepository planetRepository;
    private final ValidationHelper validationHelper;

    @Override
    @Transactional(dontRollbackOn = BpmnError.class)
    public void execute(DelegateExecution execution) {
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution);
        log.debug("addCharacterToAssaultTask for battle {}", accessor.getBusinessKey());

        Battle battle = battleRepository.getOne(accessor.getBattleId());
        Planet planet = planetRepository.getOne(accessor.getPlanetId());
        GwCharacter character = characterRepository.getOne(accessor.getRequestCharacter());

        validationHelper.validateCharacterInBattle(character, battle, false);
        validationHelper.validateCharacterFreeForGame(character);

        BattleRole battleRole;
        Integer newParticipantsOfFactionCount = 0;
        if (character.getFaction() == battle.getAttackingFaction()) {
            battleRole = BattleRole.ATTACKER;
            newParticipantsOfFactionCount = accessor.getAttackerCount() + 1;
        } else if (character.getFaction() == battle.getDefendingFaction()) {
            battleRole = BattleRole.DEFENDER;
            newParticipantsOfFactionCount = accessor.getDefenderCount() + 1;
        } else {
            battleRole = null;
        }

        validationHelper.validateOpenSlotForCharacter(battle, battleRole);
        BattleParticipant battleParticipant = new BattleParticipant(battle, character, battleRole);
        battle.getParticipants().add(battleParticipant);
        battleRepository.save(battle);

        accessor.setParticipantCount(battleRole, newParticipantsOfFactionCount);

        log.info("Character {} joined battle {}", character.getId(), battle.getId());

        if (battle.getParticipants().size() == planet.getMap().getTotalSlots()) {
            log.info("Battle {} is full", battle.getId());
            accessor.setGameFull(true);
        }
    }
}
