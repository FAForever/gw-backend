package com.faforever.gw.messaging.lobby.inbound;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerErrorException extends Exception {
    private long code;
    private String title;
    private String text;
}
