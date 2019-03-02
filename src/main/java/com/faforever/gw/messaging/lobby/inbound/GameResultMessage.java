package com.faforever.gw.messaging.lobby.inbound;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GameResultMessage extends InboundLobbyMessage {
	private long gameId;
	private List<GamePlayerResult> playerResults;
}