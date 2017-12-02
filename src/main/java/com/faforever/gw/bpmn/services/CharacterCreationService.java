package com.faforever.gw.bpmn.services;

import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.outbound.ErrorMessage;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.client.incoming.RequestCharacterMessage;
import com.faforever.gw.services.messaging.client.incoming.SelectCharacterNameMessage;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.UUID;

@Slf4j
@Service
public class CharacterCreationService {
    public static final String REQUEST_CHARACTER_MESSAGE = "Message_RequestCharacter";
    public static final String RECEIVE_CHARACTER_NAME_SELECTION_MESSAGE = "Message_ReceiveCharacterNameSelection";

    private final ProcessEngine processEngine;
    private final RuntimeService runtimeService;
    private final ClientMessagingService clientMessagingService;
    private final CharacterRepository characterRepository;

    @Inject
    public CharacterCreationService(ProcessEngine processEngine, RuntimeService runtimeService, ClientMessagingService clientMessagingService, CharacterRepository characterRepository) {
        this.processEngine = processEngine;
        this.runtimeService = runtimeService;
        this.clientMessagingService = clientMessagingService;
        this.characterRepository = characterRepository;
    }

    public void onRequestCharacter(RequestCharacterMessage message, User user) {
        log.debug("onRequestCharacter");

        VariableMap variables = clientMessagingService.createVariables(user.getId(), message.getRequestId(), null)
                .putValue("requestedFaction", message.getFaction());

        runtimeService.correlateMessage(REQUEST_CHARACTER_MESSAGE, message.getRequestId().toString(), variables);
    }

    public void onSelectCharacterName(SelectCharacterNameMessage message, User user) {
        log.debug("onSelectCharacterName");

        runtimeService.correlateMessage(RECEIVE_CHARACTER_NAME_SELECTION_MESSAGE, message.getRequestId().toString(),
                Variables.createVariables().putValue("selectedName", message.getName()));
    }

    private void sendErrorToUser(User user, UUID requestId, GwErrorType errorType) {
        clientMessagingService.sendToUser(new ErrorMessage(requestId, errorType.getErrorCode(), errorType.getErrorMessage()), user);
    }
}
