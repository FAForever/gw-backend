package com.faforever.gw.bpmn.message;

import com.faforever.gw.model.Battle;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.GwCharacter;
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
public class BattleParticipantLeftAssaultMessage implements JavaDelegate, WebsocketMessage {
    @Getter(AccessLevel.NONE)
    private final MessagingService messagingService;

    private UUID characterId;
    private UUID battleId;
    private Faction characterFaction;

    @Inject
    public BattleParticipantLeftAssaultMessage(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        GwCharacter character = (GwCharacter)execution.getVariable("character");
        Battle battle = (Battle)execution.getVariable("battle");

        characterId = character.getId();
        battleId = battle.getId();
        characterFaction = character.getFaction();

        log.debug("Sending BattleParticipantLeftAssaultMessage (characterId: {}, battleId: {}, characterFaction: {}, defendingFaction: {}", characterId, battleId, characterFaction);
        messagingService.send(this);
    }

    @Override
    public WebsocketChannel getChannel() {
        return WebsocketChannel.BATTLES_PARTICIPANT_LEFT;
    }
}
