package com.faforever.gw.bpmn.message.character_creation;

import com.faforever.gw.bpmn.accessors.CharacterCreationAccessor;
import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.outbound.CharacterNameProposalMessage;
import com.faforever.gw.security.GwUserRegistry;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Slf4j
@Service
public class CharacterNameProposalNotification implements JavaDelegate {
    private final ClientMessagingService clientMessagingService;
    private final GwUserRegistry gwUserRegistry;

    @Inject
    public CharacterNameProposalNotification(ClientMessagingService clientMessagingService, GwUserRegistry gwUserRegistry) {
        this.clientMessagingService = clientMessagingService;
        this.gwUserRegistry = gwUserRegistry;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        CharacterCreationAccessor accessor = CharacterCreationAccessor.of(execution);

        val requestId = accessor.getRequestId();
        val user = gwUserRegistry.getUser(accessor.getRequestFafUser()).orElseThrow(() -> new IllegalStateException("fafUser already logged out"));
        val proposedNamesList = accessor.getProposedNamesList();

        log.debug("Sending CharacterNameProposalMessage (fafUser: {}, requestId: {}, proposedNamesList: {})", user.getId(), requestId, proposedNamesList);
        clientMessagingService.sendToUser(new CharacterNameProposalMessage(requestId, proposedNamesList), user);
    }
}
