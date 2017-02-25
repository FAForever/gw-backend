package com.faforever.gw.bpmn.message;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import com.faforever.gw.services.messaging.MessagingService;
import com.faforever.gw.services.messaging.WebsocketChannel;
import com.faforever.gw.services.messaging.WebsocketMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.UUID;

@Slf4j
@Getter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlanetUnderAssaultMessage implements JavaDelegate, WebsocketMessage {
    @Getter(AccessLevel.NONE)
    private final MessagingService messagingService;
    @Getter(AccessLevel.NONE)
    private final PlanetRepository planetRepository;
    @Getter(AccessLevel.NONE)
    private final CharacterRepository characterRepository;

    private UUID planetId;
    private UUID battleId;
    private Faction attackingFaction;
    private Faction defendingFaction;

    @Inject
    public PlanetUnderAssaultMessage(MessagingService messagingService, PlanetRepository planetRepository, CharacterRepository characterRepository) {
        this.messagingService = messagingService;
        this.planetRepository = planetRepository;
        this.characterRepository = characterRepository;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution.getVariables());

        planetId = accessor.getPlanetId();
        battleId = accessor.getBattleId();
        attackingFaction = accessor.getAttackingFaction();
        defendingFaction = accessor.getDefendingFaction();

        log.debug("Sending PlanetUnderAssaultMessage (planetId: {}, battleId: {}, attackingFaction: {}, defendingFaction: {}", planetId, battleId, attackingFaction, defendingFaction);
        messagingService.send(this);
    }

    @Override
    public WebsocketChannel getChannel() {
        return WebsocketChannel.PLANETS_ATTACKED;
    }
}
