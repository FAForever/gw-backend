package com.faforever.gw.bpmn.task.planetary_assault;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.model.*;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import com.sun.org.apache.xpath.internal.operations.Bool;
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
        log.debug("removeCharacterFromAssaultTask");

        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution.getVariables());

        Battle battle = battleRepository.getOne(accessor.getBattleId());
        GwCharacter character = characterRepository.getOne(accessor.getLastLeftCharacter());

        try {
            validationHelper.validateCharacterInBattle(character, battle, true);

            battle.getParticipants().removeIf(battleParticipant -> battleParticipant.getCharacter() == character);
            battleRepository.save(battle);

            String countVariable = "";
            Integer newParticipantsOfFactionCount = 0;
            Boolean noMoreAttackerRemaining = false;
            if (character.getFaction() == battle.getAttackingFaction()) {
                countVariable = "attackerCount";
                newParticipantsOfFactionCount = accessor.getAttackerCount()-1;
                noMoreAttackerRemaining = (newParticipantsOfFactionCount == 0);
            } else if (character.getFaction() == battle.getDefendingFaction()) {
                countVariable = "defenderCount";
                newParticipantsOfFactionCount = accessor.getDefenderCount()-1;
            }

            execution.setVariable(countVariable, newParticipantsOfFactionCount);
            log.debug("-> set {} = {}", countVariable, newParticipantsOfFactionCount);

            execution.setVariable("gameFull", false);
            log.debug("-> set gameFull = false");

            log.info("Character {} left battle {}", character.getId(), battle.getId());

            if (noMoreAttackerRemaining) {
                log.info("Battle {} won by defender (all attacker left)", battle.getId());
                execution.setVariable("winner", BattleRole.DEFENDER.getName());
            }
        } catch (BpmnError e) {
            execution.setVariable("errorCharacter", character.getId());
            throw e;
        }
    }
}
