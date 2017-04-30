package com.faforever.gw.services.messaging.outgoing;

import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.AbstractOutgoingWebSocketMessage;
import com.faforever.gw.services.messaging.MessageType;
import lombok.Data;

import java.util.UUID;

@Data
public class HelloMessage extends AbstractOutgoingWebSocketMessage {
    private UUID characterId;
    private UUID currentBattleId;

    public HelloMessage(User user, UUID characterId, UUID currentBattleId) {
        super(user);
        this.characterId = characterId;
        this.currentBattleId = currentBattleId;
    }

    @Override
    public MessageType getAction() {
        return MessageType.HELLO;
    }
}
