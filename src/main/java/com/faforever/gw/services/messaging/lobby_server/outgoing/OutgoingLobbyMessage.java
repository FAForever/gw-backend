package com.faforever.gw.services.messaging.lobby_server.outgoing;

import com.faforever.gw.services.messaging.lobby_server.LobbyServerMessageType;

import java.util.UUID;

public interface OutgoingLobbyMessage {
    LobbyServerMessageType getAction();

    UUID getRequestId();
}
