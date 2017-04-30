package com.faforever.gw.bpmn.message.calculate_promotions;

import com.faforever.gw.bpmn.accessors.CalculatePromotionsAccessor;
import com.faforever.gw.security.GwUserRegistry;
import com.faforever.gw.services.messaging.MessagingService;
import com.faforever.gw.services.messaging.outgoing.CharacterPromotionMessage;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
public class CharacterPromotionNotification implements JavaDelegate {
    private final MessagingService messagingService;
    private final GwUserRegistry gwUserRegistry;

    @Inject
    public CharacterPromotionNotification(MessagingService messagingService, GwUserRegistry gwUserRegistry) {
        this.messagingService = messagingService;
        this.gwUserRegistry = gwUserRegistry;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        CalculatePromotionsAccessor accessor = CalculatePromotionsAccessor.of(execution);

        val characterId = accessor.getCharacter_Local();
        val newRank = accessor.getNewRank_Local();

        log.debug("Sending CharacterPromotionMessage (characterId: {}, newRank: {})", characterId, newRank);
        messagingService.send(new CharacterPromotionMessage(gwUserRegistry.getConnectedUsers(), characterId, newRank));
    }
}
