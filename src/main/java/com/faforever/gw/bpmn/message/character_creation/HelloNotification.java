package com.faforever.gw.bpmn.message.character_creation;

import com.faforever.gw.bpmn.accessors.CharacterCreationAccessor;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.security.GwUserRegistry;
import com.faforever.gw.services.messaging.client.MessagingService;
import com.faforever.gw.services.messaging.client.outgoing.HelloMessage;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
public class HelloNotification implements JavaDelegate {
    private final MessagingService messagingService;
    private final GwUserRegistry gwUserRegistry;
    private final CharacterRepository characterRepository;

    @Inject
    public HelloNotification(MessagingService messagingService, GwUserRegistry gwUserRegistry, CharacterRepository characterRepository) {
        this.messagingService = messagingService;
        this.gwUserRegistry = gwUserRegistry;
        this.characterRepository = characterRepository;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        CharacterCreationAccessor accessor = CharacterCreationAccessor.of(execution);

        val requestId = accessor.getRequestId();
        val fafUserId = accessor.getRequestFafUser();

        gwUserRegistry.getUser(fafUserId)
                .ifPresent(user -> {
                    log.debug("Sending HelloMessage (requestId: {})", requestId);

                    GwCharacter character = characterRepository.findOne(accessor.getNewCharacterId());
                    user.setActiveCharacter(character);

                    messagingService.send(new HelloMessage(user, character.getId(), null));
                });
    }
}
