package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import lombok.Data;

import java.util.UUID;

@Data
public class BattleUpdateWaitingProgressMessage extends OutboundClientMessage {
    private UUID battleId;
    private Double waitingProgress;

    public BattleUpdateWaitingProgressMessage(UUID battleId, Double waitingProgress) {
        this.battleId = battleId;
        this.waitingProgress = waitingProgress;
    }

    @Override
    public Audience getAudience() {
        return Audience.PUBLIC;
    }
}
