package com.faforever.gw.messaging.client.inbound;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class JoinAssaultMessage extends InboundClientMessage {
    private UUID battleId;
}
