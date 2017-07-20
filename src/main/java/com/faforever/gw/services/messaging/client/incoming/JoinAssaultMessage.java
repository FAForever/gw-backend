package com.faforever.gw.services.messaging.client.incoming;

import com.faforever.gw.services.messaging.client.IncomingWebSocketMessage;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@AllArgsConstructor
public class JoinAssaultMessage implements IncomingWebSocketMessage {
    private UUID requestId;
    private UUID battleId;
}
