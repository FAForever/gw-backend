package com.faforever.gw.messaging.lobby.inbound;

import lombok.Value;

import java.util.UUID;

@Value
public class ErrorMessage implements InboundLobbyMessage {
    UUID requestId;
    long code;
    String title;
    String text;
    Object args;
}
