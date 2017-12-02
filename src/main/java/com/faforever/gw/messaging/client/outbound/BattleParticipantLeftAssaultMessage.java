package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import com.faforever.gw.model.Faction;
import lombok.Value;

import java.util.UUID;

@Value
public class BattleParticipantLeftAssaultMessage extends OutboundClientMessage {
    private final UUID characterId;
    private final UUID battleId;
    private final Faction attackingFaction;
    private final Faction defendingFaction;

    public BattleParticipantLeftAssaultMessage(UUID characterId, UUID battleId, Faction attackingFaction, Faction defendingFaction) {
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
