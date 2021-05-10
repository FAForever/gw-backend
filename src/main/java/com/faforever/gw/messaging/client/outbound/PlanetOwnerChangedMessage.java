package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import com.faforever.gw.model.Faction;
import lombok.Data;
import lombok.Value;

import java.util.UUID;

@Value
public class PlanetOwnerChangedMessage implements OutboundClientMessage {
    UUID planetId;
    Faction newOwner;

    @Override
    public Audience getAudience() {
        return Audience.PUBLIC;
    }
}
