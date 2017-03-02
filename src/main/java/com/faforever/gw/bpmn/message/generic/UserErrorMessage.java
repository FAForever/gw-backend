package com.faforever.gw.bpmn.message.generic;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.security.User;
import com.faforever.gw.security.GwUserRegistry;
import com.faforever.gw.services.messaging.MessagingService;
import com.faforever.gw.services.messaging.WebsocketChannel;
import com.faforever.gw.services.messaging.WebsocketMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserErrorMessage implements JavaDelegate, WebsocketMessage {
    private final MessagingService messagingService;
    private final GwUserRegistry gwUserRegistry;

    @Setter
    private UUID errorCharacter;

    @Setter
    @Getter
    private String errorCode;
    @Getter
    @Setter
    private String errorMessage;


    @Inject
    public UserErrorMessage(MessagingService messagingService, GwUserRegistry gwUserRegistry) {
        this.messagingService = messagingService;
        this.gwUserRegistry = gwUserRegistry;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution);

        errorCode = accessor.getErrorCode();
        errorMessage = accessor.getErrorMessage();
        errorCharacter = accessor.getErrorCharacter();

        log.warn("Sending UserErrorMessage (code: {}, message: {}", errorCode, errorMessage);
        messagingService.send(this);
    }

    @Override
    public WebsocketChannel getChannel() {
        return WebsocketChannel.USER_ERROR;
    }

    @Override
    public Collection<User> getRecipients() {
        final List<User> recipients = new ArrayList<>();

        gwUserRegistry.getUser(errorCharacter).ifPresent(user -> recipients.add(user));

        return recipients;
    }
}
