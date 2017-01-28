package com.faforever.gw.bpmn.task;

import com.faforever.gw.model.*;
import com.faforever.gw.model.repository.BattleRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.UUID;

@Slf4j
@Component
@NoArgsConstructor
public class AddCharacterToAssaultTask implements JavaDelegate {
    private BattleRepository battleRepository;

    @Inject
    public AddCharacterToAssaultTask(BattleRepository battleRepository) {
        this.battleRepository = battleRepository;
    }

    @Override
    @Transactional
    public void execute(DelegateExecution execution) {
        log.debug("addCharacterToAssaultTask");

        Battle battle = battleRepository.getOne(UUID.fromString(execution.getProcessInstance().getBusinessKey()));
        GwCharacter gwCharacter = (GwCharacter) execution.getVariable("character");
        Planet planet = (Planet)execution.getVariable("planet");

        BattleRole battleRole;

        if (gwCharacter.getFaction() == battle.getAttackingFaction()) {
            battleRole = BattleRole.ATTACKER;
        } else if (gwCharacter.getFaction() == battle.getDefendingFaction()) {
            // check if enough slots available
            battleRole = BattleRole.DEFENDER;
        } else {
            throw GwError.NO_SLOTS_FOR_FACTION.asBpmnError();
        }

        long characterCount = battle.getParticipants().stream()
                .filter(battleParticipant -> battleParticipant.getRole() == battleRole)
                .count();

        if(characterCount >= (planet.getMap().getTotalSlots() / 2)){
            throw GwError.NO_SLOTS_FOR_FACTION.asBpmnError();
        }

        BattleParticipant battleParticipant = new BattleParticipant(battle, gwCharacter, battleRole);
        battle.getParticipants().add(battleParticipant);
        battleRepository.save(battle);

        execution.setVariable("gameFull", battle.getParticipants().size() == planet.getMap().getTotalSlots());
    }
}
