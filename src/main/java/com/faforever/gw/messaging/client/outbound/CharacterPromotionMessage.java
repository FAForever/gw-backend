package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import lombok.Data;

import java.util.UUID;

@Data
public class CharacterPromotionMessage extends OutboundClientMessage {
    private UUID character;
    private int newRank;

    public CharacterPromotionMessage(UUID characterId, int newRank) {
        this.character = characterId;
        this.newRank = newRank;
    }

    @Override
    public Audience getAudience() {
        return Audience.PUBLIC;
    }
}
