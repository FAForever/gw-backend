package com.faforever.gw.bpmn.message.planetary_assault;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.CharacterRepository;
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
public class BattleParticipantLeftAssaultMessage implements JavaDelegate, WebsocketMessage {
    @Getter(AccessLevel.NONE)
    private final MessagingService messagingService;
    @Getter(AccessLevel.NONE)
    private final CharacterRepository characterRepository;

    private UUID characterId;
    private UUID battleId;
    private Faction characterFaction;

    @Inject
    public BattleParticipantLeftAssaultMessage(MessagingService messagingService, CharacterRepository characterRepository) {
        this.messagingService = messagingService;
        this.characterRepository = characterRepository;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution);

        GwCharacter character = characterRepository.findOne(accessor.getLastLeftCharacter());
        characterId = character.getId();
        characterFaction = character.getFaction();

        battleId = accessor.getBattleId();

        log.debug("Sending BattleParticipantLeftAssaultMessage (characterId: {}, battleId: {}, characterFaction: {}, defendingFaction: {}", characterId, battleId, characterFaction);
        messagingService.send(this);
    }

    @Override
    public WebsocketChannel getChannel() {
        return WebsocketChannel.BATTLES_PARTICIPANT_LEFT;
    }
}
