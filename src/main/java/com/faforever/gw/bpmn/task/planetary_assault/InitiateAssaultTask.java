package com.faforever.gw.bpmn.task.planetary_assault;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.model.*;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.UUID;

@Slf4j
@Component
public class InitiateAssaultTask implements JavaDelegate {
    private final RuntimeService runtimeService;
    private final CharacterRepository characterRepository;
    private final PlanetRepository planetRepository;
    private final BattleRepository battleRepository;
    private final ValidationHelper validationHelper;

    @Inject
    public InitiateAssaultTask(RuntimeService runtimeService, CharacterRepository characterRepository, PlanetRepository planetRepository, BattleRepository battleRepository, ValidationHelper validationHelper) {
        this.runtimeService = runtimeService;
        this.characterRepository = characterRepository;
        this.planetRepository = planetRepository;
        this.battleRepository = battleRepository;
        this.validationHelper = validationHelper;
    }

    @Override
    @Transactional(dontRollbackOn = BpmnError.class)
    public void execute(DelegateExecution execution) {
        log.debug("validateAssault");

        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution.getVariables());

        log.info("Battle {} initiated by character {}", accessor.getBattleId(), accessor.getInitiatorId());

        GwCharacter character = characterRepository.findOne(accessor.getInitiatorId());
        Planet planet = planetRepository.findOne(accessor.getPlanetId());
        Faction attackingFaction = accessor.getAttackingFaction();
        Faction defendingFaction = accessor.getDefendingFaction();

        validationHelper.validateCharacterFreeForGame(character);
        validationHelper.validateAssaultOnPlanet(character, planet);

        Battle battle = new Battle(planet, attackingFaction, defendingFaction);
        battle.setId(accessor.getBattleId());
        BattleParticipant battleParticipant = new BattleParticipant(battle, character, BattleRole.ATTACKER);
        battle.getParticipants().add(battleParticipant);

        battleRepository.save(battle);

        log.debug("-> added processVariable `battle`: {}", battle.getId());
        execution.setVariable("battle", battle.getId());
    }
}
