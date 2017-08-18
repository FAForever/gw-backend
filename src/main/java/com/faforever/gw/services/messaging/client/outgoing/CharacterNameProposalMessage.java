package com.faforever.gw.services.messaging.client.outgoing;

import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.client.AbstractOutgoingWebSocketMessage;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CharacterNameProposalMessage extends AbstractOutgoingWebSocketMessage {
    private UUID requestId;
    private List<String> proposedNamesList;

    public CharacterNameProposalMessage(User user, UUID requestId, List<String> proposedNamesList) {
        super(user);

        this.requestId = requestId;
        this.proposedNamesList = proposedNamesList;
    }
}
