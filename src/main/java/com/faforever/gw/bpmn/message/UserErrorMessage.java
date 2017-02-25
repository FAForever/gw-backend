package com.faforever.gw.bpmn.message;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.security.User;
import com.faforever.gw.security.GwUserRegistry;
import com.faforever.gw.services.messaging.MessagingService;
import com.faforever.gw.services.messaging.WebsocketChannel;
import com.faforever.gw.services.messaging.WebsocketMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Slf4j
@Getter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserErrorMessage implements JavaDelegate, WebsocketMessage {
    @Getter(AccessLevel.NONE)
    private final MessagingService messagingService;
    @Getter(AccessLevel.NONE)
    private final GwUserRegistry gwUserRegistry;

    @Getter(AccessLevel.NONE)
    private UUID errorCharacter;

    private String errorCode;
    private String errorMessage;


    @Inject
    public UserErrorMessage(MessagingService messagingService, GwUserRegistry gwUserRegistry) {
        this.messagingService = messagingService;
        this.gwUserRegistry = gwUserRegistry;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution.getVariables());

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
