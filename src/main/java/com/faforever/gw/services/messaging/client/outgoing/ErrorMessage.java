package com.faforever.gw.services.messaging.client.outgoing;

import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.client.AbstractOutgoingWebSocketMessage;
import com.faforever.gw.services.messaging.client.MessageType;
import lombok.Data;

import java.util.UUID;

@Data
public class ErrorMessage extends AbstractOutgoingWebSocketMessage {
    private UUID requestId;
    private String errorCode;
    private String errorMessage;

    public ErrorMessage(User user, UUID requestId, String errorCode, String errorMessage) {
        super(user);
        this.requestId = requestId;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public MessageType getAction() {
        return MessageType.ERROR;
    }
}
