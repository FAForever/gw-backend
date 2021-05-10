package com.faforever.gw.messaging.client.inbound;

import lombok.Value;

import java.util.UUID;

@Value
public class UnlinkSolarSystemsRequestMessage implements InboundClientMessage {
    UUID requestId = UUID.randomUUID();

    UUID solarSystemFrom;
    UUID solarSystemTo;
}
