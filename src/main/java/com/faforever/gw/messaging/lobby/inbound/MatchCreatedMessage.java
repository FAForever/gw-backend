package com.faforever.gw.messaging.lobby.inbound;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class MatchCreatedMessage extends InboundLobbyMessage implements ResponseMessage {
    UUID requestId;
    long gameId;
}
