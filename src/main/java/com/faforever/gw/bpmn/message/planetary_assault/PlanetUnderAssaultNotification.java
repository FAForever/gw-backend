package com.faforever.gw.bpmn.message.planetary_assault;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.security.GwUserRegistry;
import com.faforever.gw.services.messaging.MessagingService;
import com.faforever.gw.services.messaging.outgoing.PlanetUnderAssaultMessage;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
public class PlanetUnderAssaultNotification implements JavaDelegate {
    private final MessagingService messagingService;
    private final GwUserRegistry gwUserRegistry;

    @Inject
    public PlanetUnderAssaultNotification(MessagingService messagingService, GwUserRegistry gwUserRegistry) {
        this.messagingService = messagingService;
        this.gwUserRegistry = gwUserRegistry;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution);

        val planetId = accessor.getPlanetId();
        val battleId = accessor.getBattleId();
        val attackingFaction = accessor.getAttackingFaction();
        val defendingFaction = accessor.getDefendingFaction();

        log.debug("Sending PlanetUnderAssaultMessage (planetId: {}, battleId: {}, attackingFaction: {}, defendingFaction: {})",
                planetId, battleId, attackingFaction, defendingFaction);
        messagingService.send(new PlanetUnderAssaultMessage(gwUserRegistry.getConnectedUsers(), planetId, battleId, attackingFaction, defendingFaction));
    }
}
