package com.faforever.gw.messaging.lobby.inbound;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CreateGameResultMessage extends InboundLobbyMessage {
    private UUID battleId;
    private boolean gameStarted;
    private List<Long> uninitializablePlayers;
}
