package com.faforever.gw.websocket.incoming;

import lombok.AllArgsConstructor;
import lombok.Value;

import javax.inject.Inject;
import java.util.UUID;

@Value
@AllArgsConstructor
public class InitiateAttackMessage {
    private UUID planetId;
}