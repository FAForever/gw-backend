package com.faforever.gw.bpmn.message.generic;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.bpmn.accessors.UserInteractionProcessAccessor;
import com.faforever.gw.security.GwUserRegistry;
import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.MessagingService;
import com.faforever.gw.services.messaging.WebsocketChannel;
import com.faforever.gw.services.messaging.WebsocketMessage;
import lombok.Getter;
import lombok.Setter;
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
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserAckMessage implements JavaDelegate, WebsocketMessage {
    private final MessagingService messagingService;
    private final GwUserRegistry gwUserRegistry;

    @Setter
    @Getter
    private UUID requestId;

    @Setter
    private UUID recipientCharacter;

    @Inject
    public UserAckMessage(MessagingService messagingService, GwUserRegistry gwUserRegistry) {
        this.messagingService = messagingService;
        this.gwUserRegistry = gwUserRegistry;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        UserInteractionProcessAccessor accessor = UserInteractionProcessAccessor.of(execution);

        requestId = accessor.getRequestId();
        recipientCharacter = accessor.getRequestCharacter();

        log.debug("Sending UserAckMessage (requestId: {}", requestId);
        messagingService.send(this);
    }

    @Override
    public WebsocketChannel getChannel() {
        return WebsocketChannel.USER_ACK;
    }


    @Override
    public Collection<User> getRecipients() {
        final List<User> recipients = new ArrayList<>();

        gwUserRegistry.getUser(recipientCharacter).ifPresent(user -> recipients.add(user));

        return recipients;
    }
}
