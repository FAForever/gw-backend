package com.faforever.gw.services.messaging.lobby_server.incoming;

import com.faforever.gw.model.BattleParticipantResult;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class GamePlayerResult {
    long playerFafId;
    BattleParticipantResult result;
    Long killedBy;
}
