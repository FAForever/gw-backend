package com.faforever.gw.websocket.incoming;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@AllArgsConstructor
public class LeaveAssaultMessage {
    private UUID battleId;
}
