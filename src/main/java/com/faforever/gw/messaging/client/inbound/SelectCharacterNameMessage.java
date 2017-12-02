package com.faforever.gw.messaging.client.inbound;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SelectCharacterNameMessage extends InboundClientMessage {
    private String name;
}
