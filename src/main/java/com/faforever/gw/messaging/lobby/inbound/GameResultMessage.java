package com.faforever.gw.messaging.lobby.inbound;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class GameResultMessage extends InboundLobbyMessage {
    private int gameId;
    private boolean draw;
    private Set<PlayerResult> playerResults;

    @Getter
    @Setter
    public static class PlayerResult {
        private int playerId;
        private boolean winner;
        private boolean acuKilled;
    }
}