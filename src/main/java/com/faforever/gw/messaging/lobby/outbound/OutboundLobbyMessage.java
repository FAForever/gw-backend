package com.faforever.gw.messaging.lobby.outbound;

import com.faforever.gw.messaging.lobby.LobbyMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public interface OutboundLobbyMessage extends LobbyMessage {
    UUID requestId();
}
