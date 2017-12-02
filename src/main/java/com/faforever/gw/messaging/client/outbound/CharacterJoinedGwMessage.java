package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import com.faforever.gw.model.Faction;
import lombok.Data;

import java.util.UUID;

@Data
public class CharacterJoinedGwMessage extends OutboundClientMessage {
    private UUID character;
    private Faction faction;
    private String name;

    public CharacterJoinedGwMessage(UUID characterId, Faction faction, String name) {
        this.character = characterId;
        this.faction = faction;
        this.name = name;
    }

    @Override
    public Audience getAudience() {
        return Audience.PUBLIC;
    }
}
