package com.faforever.gw.messaging.lobby.inbound;

import lombok.Value;

import java.util.List;

/**
 * Message sent from the server to the client containing information about players.
 */
@Value
public class PlayersMessage implements InboundLobbyMessage {
    List<PlayerSubMessage> players;
}
