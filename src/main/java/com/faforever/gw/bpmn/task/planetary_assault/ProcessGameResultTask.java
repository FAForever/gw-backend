package com.faforever.gw.bpmn.task.planetary_assault;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.model.BattleRole;
import com.faforever.gw.model.GameResult;
import com.faforever.gw.model.service.BattleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessGameResultTask implements JavaDelegate {
    private final BattleService battleService;

    @Override
    @Transactional(dontRollbackOn = BpmnError.class)
    public void execute(DelegateExecution execution) throws Exception {
        // Information: we don't change the actual battle here - see CloseAssaultTask instead
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution);
        log.debug("processGameResultTask for battle {}", accessor.getBusinessKey());

        GameResult gameResult = accessor.getGameResult();
        accessor.setWinner(gameResult.getWinner() == accessor.getAttackingFaction() ? BattleRole.ATTACKER : BattleRole.DEFENDER);

        battleService.processGameResult(accessor.getGameResult());
    }
}
