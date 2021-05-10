package com.faforever.gw.bpmn.message.character_creation;

import com.faforever.gw.bpmn.accessors.CharacterCreationAccessor;
import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.outbound.CharacterNameProposalMessage;
import com.faforever.gw.security.User;
import com.faforever.gw.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CharacterNameProposalNotification implements JavaDelegate {
    private final ClientMessagingService clientMessagingService;
    private final UserService userService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        CharacterCreationAccessor accessor = CharacterCreationAccessor.of(execution);

        UUID requestId = accessor.getRequestId();
        User user = userService.getOnlineUserByFafId(accessor.getRequestFafUser())
                .orElseThrow(() -> new IllegalStateException("fafUser already logged out"));
        List<String> proposedNamesList = accessor.getProposedNamesList();

        log.debug("Sending CharacterNameProposalMessage (requestId: {}, proposedNamesList: {})", requestId, proposedNamesList);
        clientMessagingService.sendToUser(new CharacterNameProposalMessage(requestId, proposedNamesList), user);
    }
}
