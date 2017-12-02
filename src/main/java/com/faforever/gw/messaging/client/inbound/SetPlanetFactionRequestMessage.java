package com.faforever.gw.messaging.client.inbound;

import com.faforever.gw.model.Faction;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SetPlanetFactionRequestMessage extends InboundClientMessage {
    private UUID planetId;
    private Faction newOwner;
}
