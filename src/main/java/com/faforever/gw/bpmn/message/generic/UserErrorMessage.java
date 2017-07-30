package com.faforever.gw.bpmn.message.generic;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.security.GwUserRegistry;
import com.faforever.gw.services.messaging.client.MessagingService;
import com.faforever.gw.services.messaging.client.outgoing.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
public class UserErrorMessage implements JavaDelegate {
    private final MessagingService messagingService;
    private final GwUserRegistry gwUserRegistry;

    @Inject
    public UserErrorMessage(MessagingService messagingService, GwUserRegistry gwUserRegistry) {
        this.messagingService = messagingService;
        this.gwUserRegistry = gwUserRegistry;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution);

        val requestId = accessor.getRequestId();
        val errorCode = accessor.getErrorCode();
        val errorMessage = accessor.getErrorMessage();
        val requestFafUser = accessor.getRequestFafUser();

        gwUserRegistry.getUser(requestFafUser)
                .ifPresent(user -> {
                    log.debug("Sending UserErrorMessage (code: {}, message: {})", errorCode, errorMessage);
                    messagingService.send(new ErrorMessage(user, requestId, errorCode, errorMessage));
                });
    }
}
