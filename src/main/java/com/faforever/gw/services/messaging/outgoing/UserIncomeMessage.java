package com.faforever.gw.services.messaging.outgoing;

import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.AbstractOutgoingWebSocketMessage;
import com.faforever.gw.services.messaging.MessageType;
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

    @Override
    public MessageType getAction() {
        return MessageType.USER_INCOME;
    }
}
