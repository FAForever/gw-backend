package com.faforever.gw.messaging.client.inbound;

import com.faforever.gw.messaging.client.ClientMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public interface InboundClientMessage extends ClientMessage {
    UUID getRequestId();
}
