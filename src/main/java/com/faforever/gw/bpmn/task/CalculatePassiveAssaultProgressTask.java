package com.faforever.gw.bpmn.task;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.repository.BattleRepository;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Slf4j
@Component
public class CalculatePassiveAssaultProgressTask implements JavaDelegate{
    private final ProcessEngine processEngine;
    private final BattleRepository battleRepository;

    public CalculatePassiveAssaultProgressTask(ProcessEngine processEngine, BattleRepository battleRepository) {
        this.processEngine = processEngine;
        this.battleRepository = battleRepository;
    }

    @Override
    @Transactional
    public void execute(DelegateExecution execution) throws Exception {
        log.debug("calculatePassiveAssaultProgressTask for battle {}", execution.getProcessInstance().getBusinessKey());

        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution.getVariables());

        Battle battle = battleRepository.getOne(accessor.getBattleId());
        Integer mapSlots = battle.getPlanet().getMap().getTotalSlots();
        long attackerCount = battle.getParticipants().stream()
                .filter(p -> p.getCharacter().getFaction() == accessor.getAttackingFaction())
                .count();
        long defenderCount = battle.getParticipants().stream()
                .filter(p -> p.getCharacter().getFaction() == accessor.getDefendingFaction())
                .count();

        VariableMap attackerVariables = Variables.createVariables()
                .putValue("map_slots", mapSlots)
                .putValue("faction_player_count", attackerCount);

        VariableMap defenderVariables =  Variables.createVariables()
                .putValue("map_slots", mapSlots)
                .putValue("faction_player_count", defenderCount);

        DecisionService decisionService = processEngine.getDecisionService();
        DmnDecisionTableResult attackerResult = decisionService.evaluateDecisionTableByKey("faction_influence_factor", attackerVariables);
        DmnDecisionTableResult defenderResult = decisionService.evaluateDecisionTableByKey("faction_influence_factor", defenderVariables);

        // we can securely access getFirstResult, because the DMN table gives a unique result
        Double attackerProgress = (Double)attackerResult.getFirstResult().getEntry("progress_factor");
        Double defenderProgress = (Double)defenderResult.getFirstResult().getEntry("progress_factor");

        Double progressNormalizer = mapSlots * 20.0;
        Double waitingProgressDelta = (attackerCount*attackerProgress + defenderCount*defenderProgress) / progressNormalizer;

        double newWaitingProgress = accessor.getWaitingProgress() + waitingProgressDelta;
        log.debug("-> set new waitingProgress = {}", newWaitingProgress);
        execution.setVariable("waitingProgress", newWaitingProgress);
    }
}
