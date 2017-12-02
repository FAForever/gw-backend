package com.faforever.gw.messaging.lobby.inbound;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class GameResultMessage extends InboundLobbyMessage {
    private UUID battleId;
    private long replayId;
    private List<GamePlayerResult> playerResults;
}