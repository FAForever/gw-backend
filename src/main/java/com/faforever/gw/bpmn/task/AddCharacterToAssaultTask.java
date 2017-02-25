package com.faforever.gw.bpmn.task;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.model.*;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Slf4j
@Component
public class AddCharacterToAssaultTask implements JavaDelegate {
    private final CharacterRepository characterRepository;
    private final BattleRepository battleRepository;
    private final PlanetRepository planetRepository;
    private final ValidationHelper validationHelper;

    @Inject
    public AddCharacterToAssaultTask(CharacterRepository characterRepository, BattleRepository battleRepository, PlanetRepository planetRepository, ValidationHelper validationHelper) {
        this.characterRepository = characterRepository;
        this.battleRepository = battleRepository;
        this.planetRepository = planetRepository;
        this.validationHelper = validationHelper;
    }

    @Override
    @Transactional(dontRollbackOn = BpmnError.class)
    public void execute(DelegateExecution execution) {
        log.debug("addCharacterToAssaultTask");

        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution.getVariables());

        Battle battle = battleRepository.getOne(accessor.getBattleId());
        Planet planet = planetRepository.getOne(accessor.getPlanetId());
        GwCharacter character = characterRepository.getOne(accessor.getLastJoinedCharacter());

        try {
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

            log.info("Character {} joined battle {}", character.getId(), battle.getId());

            if(battle.getParticipants().size() == planet.getMap().getTotalSlots()){
                log.info("Battle {} is full", battle.getId());
                execution.setVariable("gameFull", true);
            }
        } catch (BpmnError e) {
            execution.setVariable("errorCharacter",character.getId());
            throw e;
        }
    }
}
