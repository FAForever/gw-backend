package com.faforever.gw.bpmn.message.generic;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.outbound.ErrorMessage;
import com.faforever.gw.security.GwUserRegistry;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
public class UserErrorMessage implements JavaDelegate {
    private final ClientMessagingService clientMessagingService;
    private final GwUserRegistry gwUserRegistry;

    @Inject
    public UserErrorMessage(ClientMessagingService clientMessagingService, GwUserRegistry gwUserRegistry) {
        this.clientMessagingService = clientMessagingService;
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
                    clientMessagingService.sendToUser(new ErrorMessage(requestId, errorCode, errorMessage), user);
                });
    }
}
