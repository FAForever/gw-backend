package com.faforever.gw.bpmn.message.regular_income;

import com.faforever.gw.bpmn.accessors.RegularIncomeAccessor;
import com.faforever.gw.security.GwUserRegistry;
import com.faforever.gw.services.messaging.client.MessagingService;
import com.faforever.gw.services.messaging.client.outgoing.UserIncomeMessage;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
public class RegularIncomeNotification implements JavaDelegate {
    private final MessagingService messagingService;
    private final GwUserRegistry gwUserRegistry;

    @Inject
    public RegularIncomeNotification(MessagingService messagingService, GwUserRegistry gwUserRegistry) {
        this.messagingService = messagingService;
        this.gwUserRegistry = gwUserRegistry;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        RegularIncomeAccessor accessor = RegularIncomeAccessor.of(execution);

        val characterId = accessor.getCharacter_Local();
        val creditsDelta = accessor.getCreditsDelta_Local();
        val creditsTotal = accessor.getCreditsTotal_Local();

        gwUserRegistry.getUser(characterId)
                .ifPresent(user -> {
                    log.debug("Sending UserIncomeMessage (characterId: {}, creditsTotal: {}, creditsDelta {})", characterId, creditsTotal, creditsDelta);
                    messagingService.send(new UserIncomeMessage(user, characterId, creditsTotal, creditsDelta));
                });
    }
}
