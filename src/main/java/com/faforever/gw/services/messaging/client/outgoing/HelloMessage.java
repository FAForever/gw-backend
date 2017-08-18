package com.faforever.gw.services.messaging.client.outgoing;

import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.client.AbstractOutgoingWebSocketMessage;
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
}
