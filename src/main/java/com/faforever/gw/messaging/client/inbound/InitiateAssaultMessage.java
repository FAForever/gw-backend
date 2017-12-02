package com.faforever.gw.messaging.client.inbound;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class InitiateAssaultMessage extends InboundClientMessage {
    private UUID planetId;
}