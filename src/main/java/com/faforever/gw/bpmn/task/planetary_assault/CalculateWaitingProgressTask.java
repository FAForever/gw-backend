package com.faforever.gw.bpmn.task.planetary_assault;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.repository.BattleRepository;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Slf4j
@Component
public class CalculateWaitingProgressTask implements JavaDelegate{
    private final ProcessEngine processEngine;
    private final BattleRepository battleRepository;

    public CalculateWaitingProgressTask(ProcessEngine processEngine, BattleRepository battleRepository) {
        this.processEngine = processEngine;
        this.battleRepository = battleRepository;
    }

    @Override
    @Transactional(dontRollbackOn = BpmnError.class)
    public void execute(DelegateExecution execution) throws Exception {
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution);
        log.debug("calculatePassiveAssaultProgressTask for battle {}", accessor.getBusinessKey());

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
        accessor.setWaitingProgress(newWaitingProgress);
    }
}
