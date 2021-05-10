package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import lombok.Data;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class CharacterNameProposalMessage implements OutboundClientMessage {
    UUID requestId;
    List<String> proposedNamesList;

    @Override
    public Audience getAudience() {
        return Audience.PRIVATE;
    }
}
