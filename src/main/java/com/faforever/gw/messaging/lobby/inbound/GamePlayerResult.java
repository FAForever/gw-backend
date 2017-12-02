package com.faforever.gw.messaging.lobby.inbound;

import com.faforever.gw.model.BattleParticipantResult;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GamePlayerResult extends InboundLobbyMessage {
    private long playerFafId;
    private BattleParticipantResult result;
    private Long killedBy;
}
