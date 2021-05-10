package com.faforever.gw.messaging.client.inbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.util.UUID;

@Value
public class LinkSolarSystemsRequestMessage implements InboundClientMessage {
    UUID requestId = UUID.randomUUID();

    UUID solarSystemFrom;
    UUID solarSystemTo;
}
