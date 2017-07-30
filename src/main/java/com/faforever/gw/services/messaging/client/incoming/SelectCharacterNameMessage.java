package com.faforever.gw.services.messaging.client.incoming;

import com.faforever.gw.services.messaging.client.IncomingWebSocketMessage;
import lombok.Value;

import java.util.UUID;


@Value
public class SelectCharacterNameMessage implements IncomingWebSocketMessage {
    private final UUID requestId;
    private final String name;
}
