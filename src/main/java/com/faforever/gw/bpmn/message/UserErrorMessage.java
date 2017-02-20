package com.faforever.gw.bpmn.message;

import com.faforever.gw.model.Battle;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Planet;
import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.MessagingService;
import com.faforever.gw.services.messaging.WebsocketChannel;
import com.faforever.gw.services.messaging.WebsocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collection;

@Slf4j
@Component
public class UserErrorMessage implements JavaDelegate, WebsocketMessage {
    private final MessagingService messagingService;

    @Inject
    public UserErrorMessage(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String errorCode = (String)execution.getVariable("errorCode");
        String errorMessage = (String)execution.getVariable("errorMessage");

        log.warn("Sending UserErrorMessage (code: {}, message: {}", errorCode, errorMessage);
        messagingService.send(this);
    }

    @Override
    public WebsocketChannel getChannel() {
        return WebsocketChannel.USER_ERROR;
    }

    @Override
    public Collection<User> getRecipients() {
        return null; // TODO: Get user belonging to GwCharacter
    }
}
