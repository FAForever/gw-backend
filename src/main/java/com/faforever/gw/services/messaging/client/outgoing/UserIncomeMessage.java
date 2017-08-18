package com.faforever.gw.services.messaging.client.outgoing;

import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.client.AbstractOutgoingWebSocketMessage;
import lombok.Data;

import java.util.UUID;

@Data
public class UserIncomeMessage extends AbstractOutgoingWebSocketMessage {
    private UUID character;
    private Long creditsTotal;
    private Long creditsDelta;

    public UserIncomeMessage(User user, UUID character, Long creditsTotal, Long creditsDelta) {
        super(user);

        this.character = character;
        this.creditsTotal = creditsTotal;
        this.creditsDelta = creditsDelta;
    }
}
