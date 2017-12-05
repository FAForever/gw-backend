package com.faforever.gw.bpmn.message.generic;

import com.faforever.gw.bpmn.accessors.UserInteractionProcessAccessor;
import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.outbound.AckMessage;
import com.faforever.gw.security.User;
import com.faforever.gw.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.UUID;

@Slf4j
@Component
public class UserAckMessage implements JavaDelegate {
    private final ClientMessagingService clientMessagingService;
    private final UserService userService;

    @Inject
    public UserAckMessage(ClientMessagingService clientMessagingService, UserService userService) {
        this.clientMessagingService = clientMessagingService;
        this.userService = userService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        UserInteractionProcessAccessor accessor = UserInteractionProcessAccessor.of(execution);

        UUID requestId = accessor.getRequestId();

        User user = userService.getOnlineUserByFafId(accessor.getRequestFafUser())
                .orElseThrow(() -> new IllegalStateException("fafUser already logged out"));

        log.debug("Sending UserAckMessage (requestId: {})", requestId);
        clientMessagingService.sendToUser(new AckMessage(requestId), user);
    }
}
