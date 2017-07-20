package com.faforever.gw.services.messaging.lobby_server.outgoing;

import com.faforever.gw.services.messaging.lobby_server.LobbyServerMessageType;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@AllArgsConstructor
public class CreateGameMessage implements OutgoingLobbyMessage {
    private UUID requestId;
    private UUID battleId;

    public LobbyServerMessageType getAction() {
        return LobbyServerMessageType.CREATE_GAME;
    }
}
