package com.faforever.gw.messaging.client.inbound;

import com.faforever.gw.model.Faction;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestCharacterMessage extends InboundClientMessage {
    private Faction faction;
}