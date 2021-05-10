package com.faforever.gw.messaging.client.inbound;

import lombok.Value;

import java.util.UUID;

@Value
public class SelectCharacterNameMessage implements InboundClientMessage {
    UUID requestId = UUID.randomUUID();

    String name;
}
