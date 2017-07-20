package com.faforever.gw.services.messaging.lobby_server.incoming;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@AllArgsConstructor
public class GameResultMessage {

    private UUID battleId;
    private long replayId;
    private List<GamePlayerResult> playerResults;
}