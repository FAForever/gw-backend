package com.faforever.gw.messaging.lobby.inbound;

import lombok.Value;

import java.util.Set;

@Value
public class GameResultMessage implements InboundLobbyMessage {
    int gameId;
    boolean draw;
    Set<PlayerResult> playerResults;

    @Value
    public static class PlayerResult {
        int playerId;
        boolean winner;
        boolean acuKilled;
    }
}