package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import com.faforever.gw.model.Faction;
import lombok.Data;

import java.util.UUID;

@Data
public class PlanetOwnerChangedMessage extends OutboundClientMessage {
    private UUID planetId;
    private Faction newOwner;

    public PlanetOwnerChangedMessage(UUID planetId, Faction newOwner) {
        this.planetId = planetId;
        this.newOwner = newOwner;
    }

    @Override
    public Audience getAudience() {
        return Audience.PUBLIC;
    }
}
