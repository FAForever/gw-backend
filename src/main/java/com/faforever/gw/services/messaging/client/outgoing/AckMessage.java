package com.faforever.gw.services.messaging.client.outgoing;

import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.client.AbstractOutgoingWebSocketMessage;
import com.faforever.gw.services.messaging.client.MessageType;
import lombok.Data;

import java.util.UUID;

@Data
public class AckMessage extends AbstractOutgoingWebSocketMessage {
    private UUID requestId;

    public AckMessage(User user, UUID requestId) {
        super(user);
        this.requestId = requestId;
    }

    @Override
    public MessageType getAction() {
        return MessageType.ACK;
    }
}
