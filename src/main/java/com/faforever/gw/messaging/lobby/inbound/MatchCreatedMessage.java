package com.faforever.gw.messaging.lobby.inbound;

import lombok.Value;

import java.util.UUID;

@Value
public class MatchCreatedMessage implements InboundLobbyMessage, ResponseMessage {
    UUID requestId;
    long gameId;
}
