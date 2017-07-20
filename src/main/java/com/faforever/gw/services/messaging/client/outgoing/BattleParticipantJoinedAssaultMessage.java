package com.faforever.gw.services.messaging.client.outgoing;

import com.faforever.gw.model.Faction;
import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.client.AbstractOutgoingWebSocketMessage;
import com.faforever.gw.services.messaging.client.MessageType;
import lombok.Data;

import java.util.Collection;
import java.util.UUID;

@Data
public class BattleParticipantJoinedAssaultMessage extends AbstractOutgoingWebSocketMessage {
    private UUID characterId;
    private UUID battleId;
    private Faction attackingFaction;
    private Faction defendingFaction;

    public BattleParticipantJoinedAssaultMessage(Collection<User> connectedUsers, UUID characterId, UUID battleId, Faction attackingFaction, Faction defendingFaction) {
        super(connectedUsers, null);

        this.characterId = characterId;
        this.battleId = battleId;
        this.attackingFaction = attackingFaction;
        this.defendingFaction = defendingFaction;
    }

    @Override
    public MessageType getAction() {
        return MessageType.BATTLE_PARTICIPANT_JOINED;
    }
}
