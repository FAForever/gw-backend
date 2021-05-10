package com.faforever.gw.bpmn.message.planetary_assault;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.outbound.BattleUpdateWaitingProgressMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
@RequiredArgsConstructor
public class BattleUpdateWaitingProgressNotification implements JavaDelegate {
    private final ClientMessagingService clientMessagingService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution);

        val battleId = accessor.getBattleId();
        val waitingProgress = accessor.getWaitingProgress();

        log.debug("Sending BattleUpdateWaitingProgressMessage (battleId: {}, waitingProgress: {})", battleId, waitingProgress);
        clientMessagingService.sendToPublic(new BattleUpdateWaitingProgressMessage(battleId, waitingProgress));
    }
}
