package com.faforever.gw.bpmn.task.planetary_assault;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.BattleRole;
import com.faforever.gw.model.BattleStatus;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;

@Slf4j
@Component
public class CloseAssaultTask implements JavaDelegate {
    private final BattleRepository battleRepository;
    private final PlanetRepository planetRepository;

    @Inject
    public CloseAssaultTask(BattleRepository battleRepository, PlanetRepository planetRepository) {
        this.battleRepository = battleRepository;
        this.planetRepository = planetRepository;
    }

    @Override
    @Transactional(dontRollbackOn = BpmnError.class)
    public void execute(DelegateExecution execution) {
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution);
        log.debug("closeAssaultTask for battle {}", accessor.getBusinessKey());

        Battle battle = battleRepository.getOne(accessor.getBattleId());
        battle.setEndedAt(Timestamp.from(Instant.now()));
        battle.setStatus(BattleStatus.FINISHED);

        Planet planet = planetRepository.getOne(accessor.getPlanetId());

        if(accessor.getWinner() == BattleRole.ATTACKER) {
            battle.setWinningFaction(battle.getAttackingFaction());
            planet.setCurrentOwner(battle.getAttackingFaction());
        } else {
            battle.setWinningFaction(battle.getDefendingFaction());
        }
    }
}
