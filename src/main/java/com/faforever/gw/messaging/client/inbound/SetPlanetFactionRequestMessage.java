package com.faforever.gw.messaging.client.inbound;

import com.faforever.gw.model.Faction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.util.UUID;

@Value
public class SetPlanetFactionRequestMessage implements InboundClientMessage {
    UUID requestId = UUID.randomUUID();

    UUID planetId;
    Faction newOwner;
}
