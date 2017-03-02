package com.faforever.gw.bpmn.message.calculate_promotions;

import com.faforever.gw.bpmn.accessors.CalculatePromotionsAccessor;
import com.faforever.gw.bpmn.accessors.RegularIncomeAccessor;
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
public class CharacterPromotionMessage implements JavaDelegate, WebsocketMessage {
    private final MessagingService messagingService;
    private final GwUserRegistry gwUserRegistry;

    @Setter
    private UUID character;

    @Getter
    private Integer newRank;

    @Inject
    public CharacterPromotionMessage(MessagingService messagingService, GwUserRegistry gwUserRegistry) {
        this.messagingService = messagingService;
        this.gwUserRegistry = gwUserRegistry;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        CalculatePromotionsAccessor accessor = CalculatePromotionsAccessor.of(execution);

        character = accessor.getCharacter_Local();
        newRank = accessor.getNewRank_Local();

        log.debug("Sending CharacterPromotionMessage (character: {}, newRank: {})", character, newRank);
        messagingService.send(this);
    }

    @Override
    public WebsocketChannel getChannel() {
        return WebsocketChannel.USER_XP;
    }

    @Override
    public Collection<User> getRecipients() {
        final List<User> recipients = new ArrayList<>();

        gwUserRegistry.getUser(character).ifPresent(user -> recipients.add(user));

        return recipients;
    }
}
