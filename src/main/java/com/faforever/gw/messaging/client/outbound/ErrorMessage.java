package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import lombok.Data;

import java.util.UUID;

@Data
public class ErrorMessage extends OutboundClientMessage {
    private UUID requestId;
    private String errorCode;
    private String errorMessage;

    public ErrorMessage(UUID requestId, String errorCode, String errorMessage) {
        this.requestId = requestId;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public Audience getAudience() {
        return Audience.PRIVATE;
    }
}
