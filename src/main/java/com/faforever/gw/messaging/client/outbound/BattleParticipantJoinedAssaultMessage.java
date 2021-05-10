package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import com.faforever.gw.model.Faction;
import lombok.Data;
import lombok.Value;

import java.util.UUID;

@Value
public class BattleParticipantJoinedAssaultMessage implements OutboundClientMessage {
    UUID characterId;
    UUID battleId;
    Faction attackingFaction;
    Faction defendingFaction;

    @Override
    public Audience getAudience() {
        return Audience.PUBLIC;
    }
}
