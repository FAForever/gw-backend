package com.faforever.gw.messaging.lobby.inbound;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Message sent from the server to the client containing information about players.
 */
@Getter
@Setter
public class PlayersMessage extends InboundLobbyMessage {
    List<PlayerSubMessage> players;
}
