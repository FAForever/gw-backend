package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import lombok.Data;
import lombok.Value;

import java.util.UUID;

@Value
public class SolarSystemsUnlinkedMessage implements OutboundClientMessage {
    UUID solarSystemFrom;
    UUID solarSystemTo;

    @Override
    public Audience getAudience() {
        return Audience.PUBLIC;
    }
}
