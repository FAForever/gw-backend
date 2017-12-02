package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import com.faforever.gw.model.Faction;
import lombok.Data;

import java.util.UUID;

@Data
public class BattleParticipantJoinedAssaultMessage extends OutboundClientMessage {
    private UUID characterId;
    private UUID battleId;
    private Faction attackingFaction;
    private Faction defendingFaction;

    public BattleParticipantJoinedAssaultMessage(UUID characterId, UUID battleId, Faction attackingFaction, Faction defendingFaction) {
        this.characterId = characterId;
        this.battleId = battleId;
        this.attackingFaction = attackingFaction;
        this.defendingFaction = defendingFaction;
    }

    @Override
    public Audience getAudience() {
        return Audience.PUBLIC;
    }
}
