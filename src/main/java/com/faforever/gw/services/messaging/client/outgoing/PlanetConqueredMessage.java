package com.faforever.gw.services.messaging.client.outgoing;

import com.faforever.gw.model.Faction;
import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.client.AbstractOutgoingWebSocketMessage;
import com.faforever.gw.services.messaging.client.MessageType;
import lombok.Data;

import java.util.Collection;
import java.util.UUID;

@Data
public class PlanetConqueredMessage extends AbstractOutgoingWebSocketMessage {
    private UUID planetId;
    private UUID battleId;
    private Faction attackingFaction;
    private Faction defendingFaction;

    public PlanetConqueredMessage(Collection<User> connectedUsers, UUID planetId, UUID battleId, Faction attackingFaction, Faction defendingFaction) {
        super(connectedUsers, null);

        this.planetId = planetId;
        this.battleId = battleId;
        this.attackingFaction = attackingFaction;
        this.defendingFaction = defendingFaction;
    }

    @Override
    public MessageType getAction() {
        return MessageType.PLANET_CONQUERED;
    }
}
