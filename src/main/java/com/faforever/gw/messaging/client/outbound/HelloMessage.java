package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import lombok.Data;

import java.util.UUID;

@Data
public class HelloMessage extends OutboundClientMessage {
    private UUID characterId;
    private UUID currentBattleId;

    public HelloMessage(UUID characterId, UUID currentBattleId) {
        this.characterId = characterId;
        this.currentBattleId = currentBattleId;
    }

    @Override
    public Audience getAudience() {
        return Audience.PRIVATE;
    }
}
