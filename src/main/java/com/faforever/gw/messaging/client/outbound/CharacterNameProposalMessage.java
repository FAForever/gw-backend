package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CharacterNameProposalMessage extends OutboundClientMessage {
    private UUID requestId;
    private List<String> proposedNamesList;

    public CharacterNameProposalMessage(UUID requestId, List<String> proposedNamesList) {
        this.requestId = requestId;
        this.proposedNamesList = proposedNamesList;
    }

    @Override
    public Audience getAudience() {
        return Audience.PRIVATE;
    }
}
