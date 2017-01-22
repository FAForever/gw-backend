package com.faforever.gw.bpmn.task;

import com.faforever.gw.model.*;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Slf4j
@Component
@NoArgsConstructor
public class InitiateAssaultTask implements JavaDelegate {
    private RuntimeService runtimeService;
    private CharacterRepository characterRepository;
    private PlanetRepository planetRepository;
    private BattleRepository battleRepository;

    @Inject
    public InitiateAssaultTask(RuntimeService runtimeService, CharacterRepository characterRepository, PlanetRepository planetRepository, BattleRepository battleRepository) {
        this.runtimeService = runtimeService;
        this.characterRepository = characterRepository;
        this.planetRepository = planetRepository;
        this.battleRepository = battleRepository;
    }

    @Override
    @Transactional
    public void execute(DelegateExecution execution) {
        GwCharacter gwCharacter = (GwCharacter)execution.getVariable("character");
        Planet planet = (Planet)execution.getVariable("planet");
        Faction attackingFaction = (Faction)execution.getVariable("attackingFaction");
        Faction defendingFaction = (Faction)execution.getVariable("defendingFaction");

        Battle battle = new Battle(planet, attackingFaction, defendingFaction);
        BattleParticipant battleParticipant = new BattleParticipant(battle, gwCharacter, BattleRole.ATTACKER);
        battle.getParticipants().add(battleParticipant);

        battleRepository.save(battle);

        log.debug("validateAssault");
    }
}
