package com.faforever.gw.bpmn.task.planetary_assault;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.BattleRole;
import com.faforever.gw.model.BattleStatus;
import com.faforever.gw.model.repository.BattleRepository;
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

    @Inject
    public CloseAssaultTask(BattleRepository battleRepository) {
        this.battleRepository = battleRepository;
    }

    @Override
    @Transactional(dontRollbackOn = BpmnError.class)
    public void execute(DelegateExecution execution) {
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution);
        log.debug("closeAssaultTask for battle {}", accessor.getBusinessKey());

        Battle battle = battleRepository.findOne(accessor.getBattleId());
        battle.setEndedAt(Timestamp.from(Instant.now()));
        battle.setStatus(BattleStatus.FINISHED);

        if(accessor.getWinner() == BattleRole.ATTACKER) {
            battle.setWinningFaction(battle.getAttackingFaction());
        } else {
            battle.setWinningFaction(battle.getDefendingFaction());
        }
    }
}
