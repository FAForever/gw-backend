package com.faforever.gw.bpmn.message.character_creation;

import com.faforever.gw.bpmn.accessors.CharacterCreationAccessor;
import com.faforever.gw.security.GwUserRegistry;
import com.faforever.gw.services.messaging.client.MessagingService;
import com.faforever.gw.services.messaging.client.outgoing.CharacterNameProposalMessage;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Slf4j
@Service
public class CharacterNameProposalNotification implements JavaDelegate {
    private final MessagingService messagingService;
    private final GwUserRegistry gwUserRegistry;

    @Inject
    public CharacterNameProposalNotification(MessagingService messagingService, GwUserRegistry gwUserRegistry) {
        this.messagingService = messagingService;
        this.gwUserRegistry = gwUserRegistry;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        CharacterCreationAccessor accessor = CharacterCreationAccessor.of(execution);

        val requestId = accessor.getRequestId();
        val user = gwUserRegistry.getUser(accessor.getRequestFafUser()).orElseThrow(() -> new IllegalStateException("fafUser already logged out"));
        val proposedNamesList = accessor.getProposedNamesList();

        log.debug("Sending CharacterNameProposalMessage (fafUser: {}, requestId: {}, proposedNamesList: {})", user.getId(), requestId, proposedNamesList);
        messagingService.send(new CharacterNameProposalMessage(user, requestId, proposedNamesList));
    }
}
