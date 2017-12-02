package com.faforever.gw.bpmn.message.generic;

import com.faforever.gw.bpmn.accessors.UserInteractionProcessAccessor;
import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.outbound.AckMessage;
import com.faforever.gw.security.GwUserRegistry;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
public class UserAckMessage implements JavaDelegate {
    private final ClientMessagingService clientMessagingService;
    private final GwUserRegistry gwUserRegistry;

    @Inject
    public UserAckMessage(ClientMessagingService clientMessagingService, GwUserRegistry gwUserRegistry) {
        this.clientMessagingService = clientMessagingService;
        this.gwUserRegistry = gwUserRegistry;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        UserInteractionProcessAccessor accessor = UserInteractionProcessAccessor.of(execution);

        val requestId = accessor.getRequestId();
        val fafUserId = accessor.getRequestFafUser();

        gwUserRegistry.getUser(fafUserId)
                .ifPresent(user -> {
                    log.debug("Sending UserAckMessage (requestId: {})", requestId);
                    clientMessagingService.sendToUser(new AckMessage(requestId), user);
                });
    }
}
