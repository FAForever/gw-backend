package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import lombok.Data;
import lombok.Value;

import java.util.UUID;

@Value
public class HelloMessage implements OutboundClientMessage {
    UUID characterId;
    UUID currentBattleId;

    @Override
    public Audience getAudience() {
        return Audience.PRIVATE;
    }
}
