package com.faforever.gw.bpmn.message.calculate_promotions;

import com.faforever.gw.bpmn.accessors.CalculatePromotionsAccessor;
import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.outbound.CharacterPromotionMessage;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
public class CharacterPromotionNotification implements JavaDelegate {
    private final ClientMessagingService clientMessagingService;

    @Inject
    public CharacterPromotionNotification(ClientMessagingService clientMessagingService) {
        this.clientMessagingService = clientMessagingService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        CalculatePromotionsAccessor accessor = CalculatePromotionsAccessor.of(execution);

        val characterId = accessor.getCharacter_Local();
        val newRank = accessor.getNewRank_Local();

        log.debug("Sending CharacterPromotionMessage (characterId: {}, newRank: {})", characterId, newRank);
        clientMessagingService.sendToPublic(new CharacterPromotionMessage(characterId, newRank));
    }
}
