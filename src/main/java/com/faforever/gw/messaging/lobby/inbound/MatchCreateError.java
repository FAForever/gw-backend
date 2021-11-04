package com.faforever.gw.messaging.lobby.inbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public record MatchCreateError(
        @JsonProperty("error_code")
        @NotNull
        String errorCode,
        Object args
) implements InboundLobbyMessage {
}
