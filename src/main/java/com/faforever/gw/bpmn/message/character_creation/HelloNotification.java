package com.faforever.gw.bpmn.message.character_creation;

import com.faforever.gw.bpmn.accessors.CharacterCreationAccessor;
import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.outbound.HelloMessage;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.UUID;

@Slf4j
@Component
public class HelloNotification implements JavaDelegate {
    private final ClientMessagingService clientMessagingService;

    @Inject
    public HelloNotification(ClientMessagingService clientMessagingService) {
        this.clientMessagingService = clientMessagingService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        CharacterCreationAccessor accessor = CharacterCreationAccessor.of(execution);

        UUID characterId = accessor.getNewCharacterId();
        log.debug("Sending HelloMessage (characterId: {})", characterId);
        clientMessagingService.sendToCharacter(new HelloMessage(characterId, null), characterId);
    }
}