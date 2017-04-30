package com.faforever.gw.services.messaging.outgoing;

import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.AbstractOutgoingWebSocketMessage;
import com.faforever.gw.services.messaging.MessageType;
import lombok.Data;

import java.util.Collection;
import java.util.UUID;

@Data
public class BattleUpdateWaitingProgressMessage extends AbstractOutgoingWebSocketMessage {
    private UUID battleId;
    private Double waitingProgress;

    public BattleUpdateWaitingProgressMessage(Collection<User> connectedUsers, UUID battleId, Double waitingProgress) {
        super(connectedUsers, null);

        this.battleId = battleId;
        this.waitingProgress = waitingProgress;
    }

    @Override
    public MessageType getAction() {
        return MessageType.BATTLE_WAITING_PROGRESS;
    }
}
