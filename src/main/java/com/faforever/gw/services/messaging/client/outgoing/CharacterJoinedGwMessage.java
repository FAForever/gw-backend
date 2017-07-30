package com.faforever.gw.services.messaging.client.outgoing;

import com.faforever.gw.model.Faction;
import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.client.AbstractOutgoingWebSocketMessage;
import com.faforever.gw.services.messaging.client.MessageType;
import lombok.Data;

import java.util.Collection;
import java.util.UUID;

@Data
public class CharacterJoinedGwMessage extends AbstractOutgoingWebSocketMessage {
    private UUID character;
    private Faction faction;
    private String name;

    public CharacterJoinedGwMessage(Collection<User> userList, UUID characterId, Faction faction, String name) {
        super(userList, null);
        this.character = characterId;
        this.faction = faction;
        this.name = name;
    }

    @Override
    public MessageType getAction() {
        return MessageType.CHARACTER_JOINED_GW;
    }
}
