package com.faforever.gw.bpmn.message.regular_income;

import com.faforever.gw.bpmn.accessors.RegularIncomeAccessor;
import com.faforever.gw.security.GwUserRegistry;
import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.MessagingService;
import com.faforever.gw.services.messaging.WebsocketChannel;
import com.faforever.gw.services.messaging.WebsocketMessage;
import com.google.common.annotations.GwtIncompatible;
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
public class RegularIncomeMessage implements JavaDelegate, WebsocketMessage {
    private final MessagingService messagingService;
    private final GwUserRegistry gwUserRegistry;

    @Setter
    private UUID character;

    @Getter
    private Long creditsTotal;
    @Getter
    private Long creditsDelta;

    @Inject
    public RegularIncomeMessage(MessagingService messagingService, GwUserRegistry gwUserRegistry) {
        this.messagingService = messagingService;
        this.gwUserRegistry = gwUserRegistry;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        RegularIncomeAccessor accessor = RegularIncomeAccessor.of(execution);

        character = accessor.getCharacter_Local();
        creditsDelta = accessor.getCreditsDelta_Local();
        creditsTotal = accessor.getCreditsTotal_Local();

        log.debug("Sending RegularIncomeMessage (character: {}, creditsTotal: {}, creditsDelta {})", character, creditsTotal, creditsDelta);
        messagingService.send(this);
    }

    @Override
    public WebsocketChannel getChannel() {
        return WebsocketChannel.USER_INCOME;
    }

    @Override
    public Collection<User> getRecipients() {
        final List<User> recipients = new ArrayList<>();

        gwUserRegistry.getUser(character).ifPresent(user -> recipients.add(user));

        return recipients;
    }
}
