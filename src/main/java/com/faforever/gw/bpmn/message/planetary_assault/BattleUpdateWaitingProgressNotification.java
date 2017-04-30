package com.faforever.gw.bpmn.message.planetary_assault;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.security.GwUserRegistry;
import com.faforever.gw.services.messaging.MessagingService;
import com.faforever.gw.services.messaging.outgoing.BattleUpdateWaitingProgressMessage;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
public class BattleUpdateWaitingProgressNotification implements JavaDelegate {
    private final MessagingService messagingService;
    private final GwUserRegistry gwUserRegistry;

    @Inject
    public BattleUpdateWaitingProgressNotification(MessagingService messagingService, GwUserRegistry gwUserRegistry) {
        this.messagingService = messagingService;
        this.gwUserRegistry = gwUserRegistry;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution);

        val battleId = accessor.getBattleId();
        val waitingProgress = accessor.getWaitingProgress();

        log.debug("Sending BattleUpdateWaitingProgressMessage (battleId: {}, waitingProgress: {})", battleId, waitingProgress);
        messagingService.send(new BattleUpdateWaitingProgressMessage(gwUserRegistry.getConnectedUsers(), battleId, waitingProgress));
    }
}
