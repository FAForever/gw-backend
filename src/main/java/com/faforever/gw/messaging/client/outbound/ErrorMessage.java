package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import lombok.Data;
import lombok.Value;

import java.util.UUID;

@Value
public class ErrorMessage implements OutboundClientMessage {
    UUID requestId;
    String errorCode;
    String errorMessage;

    @Override
    public Audience getAudience() {
        return Audience.PRIVATE;
    }
}
