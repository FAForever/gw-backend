package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import lombok.Data;
import lombok.Value;

import java.util.UUID;

@Value
public class UserIncomeMessage implements OutboundClientMessage {
    UUID character;
    Long creditsTotal;
    Long creditsDelta;

    @Override
    public Audience getAudience() {
        return Audience.PRIVATE;
    }
}
