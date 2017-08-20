package com.faforever.gw.services.messaging.client.outgoing;

import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.client.AbstractOutgoingWebSocketMessage;
import lombok.Data;

import java.util.Collection;
import java.util.UUID;

@Data
public class SolarSystemsUnlinkedMessage extends AbstractOutgoingWebSocketMessage {
    private UUID solarSystemFrom;
    private UUID solarSystemTo;

    public SolarSystemsUnlinkedMessage(Collection<User> connectedUsers, UUID solarSystemFrom, UUID solarSystemTo) {
        super(connectedUsers, null);

        this.solarSystemFrom = solarSystemFrom;
        this.solarSystemTo = solarSystemTo;
    }
}
