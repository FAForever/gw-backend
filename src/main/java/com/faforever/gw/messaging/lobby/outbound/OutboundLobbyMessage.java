package com.faforever.gw.messaging.lobby.outbound;

import com.faforever.gw.messaging.lobby.LobbyMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class OutboundLobbyMessage extends LobbyMessage {
    private UUID requestId;

    protected OutboundLobbyMessage() {
        requestId = UUID.randomUUID();
    }
}
