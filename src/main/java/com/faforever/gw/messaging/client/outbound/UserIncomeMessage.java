package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import lombok.Data;

import java.util.UUID;

@Data
public class UserIncomeMessage extends OutboundClientMessage {
    private UUID character;
    private Long creditsTotal;
    private Long creditsDelta;

    public UserIncomeMessage(UUID character, Long creditsTotal, Long creditsDelta) {
        this.character = character;
        this.creditsTotal = creditsTotal;
        this.creditsDelta = creditsDelta;
    }

    @Override
    public Audience getAudience() {
        return Audience.PRIVATE;
    }
}
