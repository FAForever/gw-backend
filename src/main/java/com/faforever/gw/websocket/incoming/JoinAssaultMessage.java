package com.faforever.gw.websocket.incoming;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@AllArgsConstructor
public class JoinAssaultMessage {
    private UUID battleId;
}
