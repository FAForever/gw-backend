package com.faforever.gw.bpmn.message.generic;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.outbound.ErrorMessage;
import com.faforever.gw.security.User;
import com.faforever.gw.services.UserService;
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
    private final UserService userService;

    @Inject
    public UserErrorMessage(ClientMessagingService clientMessagingService, UserService userService) {
        this.clientMessagingService = clientMessagingService;
        this.userService = userService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution);

        val requestId = accessor.getRequestId();
        val errorCode = accessor.getErrorCode();
        val errorMessage = accessor.getErrorMessage();
        val requestFafUser = accessor.getRequestFafUser();

        User user = userService.getOnlineUserByFafId(requestFafUser)
                .orElseThrow(() -> new IllegalStateException("fafUser already logged out"));

        log.debug("Sending UserErrorMessage (code: {}, message: {})", errorCode, errorMessage);
        clientMessagingService.sendToUser(new ErrorMessage(requestId, errorCode, errorMessage), user);
    }
}
