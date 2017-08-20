package com.faforever.gw.services.messaging.client.incoming;

import com.faforever.gw.model.Faction;
import com.faforever.gw.services.messaging.client.IncomingWebSocketMessage;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@AllArgsConstructor
public class SetPlanetFactionRequestMessage implements IncomingWebSocketMessage {
    private UUID requestId;
    private UUID planetId;
    private Faction newOwner;
}
