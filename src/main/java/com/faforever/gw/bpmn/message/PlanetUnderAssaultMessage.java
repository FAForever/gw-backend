package com.faforever.gw.bpmn.message;

import com.faforever.gw.model.Battle;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Planet;
import com.faforever.gw.services.messaging.MessagingService;
import com.faforever.gw.services.messaging.WebsocketChannel;
import com.faforever.gw.services.messaging.WebsocketMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.UUID;

@Slf4j
@Getter
@Component
public class PlanetUnderAssaultMessage implements JavaDelegate, WebsocketMessage {
    @Getter(AccessLevel.NONE)
    private final MessagingService messagingService;

    private UUID planetId;
    private UUID battleId;
    private Faction attackingFaction;
    private Faction defendingFaction;

    @Inject
    public PlanetUnderAssaultMessage(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Planet planet = (Planet)execution.getVariable("planet");
        GwCharacter character = (GwCharacter)execution.getVariable("initiator");
        Battle battle = (Battle)execution.getVariable("battle");

        planetId = planet.getId();
        battleId = battle.getId();
        attackingFaction = character.getFaction();
        defendingFaction = planet.getCurrentOwner();

        log.debug("Sending PlanetUnderAssaultMessage (planetId: {}, battleId: {}, attackingFaction: {}, defendingFaction: {}", planetId, battleId, attackingFaction, defendingFaction);
        messagingService.send(this);
    }

    @Override
    public WebsocketChannel getChannel() {
        return WebsocketChannel.PLANETS_ATTACKED;
    }
}
