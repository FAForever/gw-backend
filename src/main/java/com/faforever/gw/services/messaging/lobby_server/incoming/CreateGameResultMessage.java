package com.faforever.gw.services.messaging.lobby_server.incoming;

import java.util.List;
import java.util.UUID;

public class CreateGameResultMessage {
    UUID battleId;
    boolean gameStarted;
    List<Long> uninitializablePlayers;
}
