package com.faforever.gw.bpmn.task.planetary_assault;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.bpmn.services.PlanetaryAssaultService;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.repository.BattleRepository;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;

@Slf4j
@Component
public class CalculateWaitingProgressTask implements JavaDelegate{
    private final PlanetaryAssaultService planetaryAssaultService;
    private final BattleRepository battleRepository;

    public CalculateWaitingProgressTask(PlanetaryAssaultService planetaryAssaultService, BattleRepository battleRepository) {
        this.planetaryAssaultService = planetaryAssaultService;
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
                .filter(p -> p.getFaction() == battle.getAttackingFaction())
                .count();
        long defenderCount = battle.getParticipants().stream()
                .filter(p -> p.getFaction() == battle.getDefendingFaction())
                .count();

        double waitingProgressDelta = planetaryAssaultService.calcWaitingProgress(mapSlots, attackerCount, defenderCount);
        double newWaitingProgress = accessor.getWaitingProgress() + waitingProgressDelta;
        battle.setWaitingProgress(newWaitingProgress);
        accessor.setWaitingProgress(newWaitingProgress);
    }
}
