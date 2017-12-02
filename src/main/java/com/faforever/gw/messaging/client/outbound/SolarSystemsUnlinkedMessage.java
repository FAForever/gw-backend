package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import lombok.Data;

import java.util.UUID;

@Data
public class SolarSystemsUnlinkedMessage extends OutboundClientMessage {
    private UUID solarSystemFrom;
    private UUID solarSystemTo;

    public SolarSystemsUnlinkedMessage(UUID solarSystemFrom, UUID solarSystemTo) {
        this.solarSystemFrom = solarSystemFrom;
        this.solarSystemTo = solarSystemTo;
    }

    @Override
    public Audience getAudience() {
        return Audience.PUBLIC;
    }
}
