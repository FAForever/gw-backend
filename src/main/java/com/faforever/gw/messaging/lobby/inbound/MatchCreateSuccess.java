package com.faforever.gw.messaging.lobby.inbound;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MatchCreateSuccess(
        @JsonProperty("game_id")
        long gameId
) implements InboundLobbyMessage {
}
