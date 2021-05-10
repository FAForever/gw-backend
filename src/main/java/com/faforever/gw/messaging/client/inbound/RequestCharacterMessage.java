package com.faforever.gw.messaging.client.inbound;

import com.faforever.gw.model.Faction;
import lombok.Value;

import java.util.UUID;

@Value
public class RequestCharacterMessage implements InboundClientMessage {
    UUID requestId = UUID.randomUUID();

    Faction faction;
}