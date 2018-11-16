package com.faforever.gw.bpmn.message.regular_income;

import com.faforever.gw.bpmn.accessors.RegularIncomeAccessor;
import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.outbound.UserIncomeMessage;
import com.faforever.gw.security.GwUserRegistry;
import com.faforever.gw.services.UserService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
public class RegularIncomeNotification implements JavaDelegate {
    private final ClientMessagingService clientMessagingService;
    private final GwUserRegistry gwUserRegistry;
    private final UserService userService;

    @Inject
    public RegularIncomeNotification(ClientMessagingService clientMessagingService, GwUserRegistry gwUserRegistry, UserService userService) {
        this.clientMessagingService = clientMessagingService;
        this.gwUserRegistry = gwUserRegistry;
        this.userService = userService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        RegularIncomeAccessor accessor = RegularIncomeAccessor.of(execution);

        val characterId = accessor.getCharacter_Local();
        val creditsDelta = accessor.getCreditsDelta_Local();
        val creditsTotal = accessor.getCreditsTotal_Local();

        log.debug("Sending UserIncomeMessage (characterId: {}, creditsTotal: {}, creditsDelta {})", characterId, creditsTotal, creditsDelta);
        clientMessagingService.sendToCharacter(new UserIncomeMessage(characterId, creditsTotal, creditsDelta), characterId);
    }
}
