package com.faforever.gw.services.messaging.incoming;

import com.faforever.gw.services.messaging.IncomingWebSocketMessage;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@AllArgsConstructor
public class InitiateAssaultMessage implements IncomingWebSocketMessage {
    private UUID requestId;
    private UUID planetId;
}