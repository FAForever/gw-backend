package com.faforever.gw.messaging.client.inbound;

import com.faforever.gw.messaging.client.ClientMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class InboundClientMessage extends ClientMessage {
    private UUID requestId;

    protected InboundClientMessage() {
        requestId = UUID.randomUUID();
    }
}
