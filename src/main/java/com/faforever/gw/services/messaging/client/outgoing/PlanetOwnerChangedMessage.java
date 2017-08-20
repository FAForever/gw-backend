package com.faforever.gw.services.messaging.client.outgoing;

import com.faforever.gw.model.Faction;
import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.client.AbstractOutgoingWebSocketMessage;
import lombok.Data;

import java.util.Collection;
import java.util.UUID;

@Data
public class PlanetOwnerChangedMessage extends AbstractOutgoingWebSocketMessage {
    private UUID planetId;
    private Faction newOwner;

    public PlanetOwnerChangedMessage(Collection<User> connectedUsers, UUID planetId, Faction newOwner) {
        super(connectedUsers, null);

        this.planetId = planetId;
        this.newOwner = newOwner;
    }
}
