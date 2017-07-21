package com.faforever.gw.services.messaging.lobby_server.outgoing;

import com.faforever.gw.services.messaging.lobby_server.LobbyServerMessageType;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@AllArgsConstructor
public class CreateGameMessage implements OutgoingLobbyMessage {
    private UUID requestId;
    private UUID battleId;
    private List<Long> participants;

    public LobbyServerMessageType getAction() {
        return LobbyServerMessageType.CREATE_GAME;
    }
}
