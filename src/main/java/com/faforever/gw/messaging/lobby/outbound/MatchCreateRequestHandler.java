package com.faforever.gw.messaging.lobby.outbound;

@FunctionalInterface
public interface MatchCreateRequestHandler {
    void send(MatchCreateRequest request);
}