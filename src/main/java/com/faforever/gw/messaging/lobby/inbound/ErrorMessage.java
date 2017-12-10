package com.faforever.gw.messaging.lobby.inbound;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ErrorMessage extends InboundLobbyMessage {
    private UUID requestId;
    private long code;
    private String title;
    private String text;
    private Object args;
}
