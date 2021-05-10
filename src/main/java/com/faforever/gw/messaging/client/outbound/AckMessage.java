package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import lombok.Data;
import lombok.Value;

import java.util.UUID;

@Value
public class AckMessage implements OutboundClientMessage {
    UUID requestId;

    @Override
    public Audience getAudience() {
        return Audience.PRIVATE;
    }
}
