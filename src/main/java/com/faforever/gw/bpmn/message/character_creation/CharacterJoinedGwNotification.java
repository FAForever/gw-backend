package com.faforever.gw.bpmn.message.character_creation;

import com.faforever.gw.bpmn.accessors.CharacterCreationAccessor;
import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.outbound.CharacterJoinedGwMessage;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
public class CharacterJoinedGwNotification implements JavaDelegate {
    private final ClientMessagingService clientMessagingService;

    @Inject
    public CharacterJoinedGwNotification(ClientMessagingService clientMessagingService) {
        this.clientMessagingService = clientMessagingService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        CharacterCreationAccessor accessor = CharacterCreationAccessor.of(execution);

        val newCharacter = accessor.getNewCharacterId();
        val faction = accessor.getRequestedFaction();
        val name = accessor.getSelectedName();

        log.debug("Sending CharacterJoinedGwMessage (characterId: {}, faction: {}, name: {})", newCharacter, faction, name);
        clientMessagingService.sendToPublic(new CharacterJoinedGwMessage(newCharacter, faction, name));
    }
}
