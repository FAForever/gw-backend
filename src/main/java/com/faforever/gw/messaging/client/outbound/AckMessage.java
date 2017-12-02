package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import lombok.Data;

import java.util.UUID;

@Data
public class AckMessage extends OutboundClientMessage {
    private UUID requestId;

    public AckMessage(UUID requestId) {
        this.requestId = requestId;
    }

    @Override
    public Audience getAudience() {
        return Audience.PRIVATE;
    }
}
