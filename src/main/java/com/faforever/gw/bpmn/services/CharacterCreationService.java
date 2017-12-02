package com.faforever.gw.bpmn.services;

import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.inbound.RequestCharacterMessage;
import com.faforever.gw.messaging.client.inbound.SelectCharacterNameMessage;
import com.faforever.gw.security.User;
import com.faforever.gw.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CharacterCreationService {
    public static final String REQUEST_CHARACTER_MESSAGE = "Message_RequestCharacter";
    public static final String RECEIVE_CHARACTER_NAME_SELECTION_MESSAGE = "Message_ReceiveCharacterNameSelection";

    private final RuntimeService runtimeService;
    private final ClientMessagingService clientMessagingService;
    private final UserService userService;

    public CharacterCreationService(RuntimeService runtimeService, ClientMessagingService clientMessagingService, UserService userService) {
        this.runtimeService = runtimeService;
        this.clientMessagingService = clientMessagingService;
        this.userService = userService;
    }

    @EventListener
    public void onRequestCharacter(RequestCharacterMessage message) {
        User user = userService.getUserFromContext();
        log.debug("onRequestCharacter by user {}", user.getId());

        VariableMap variables = clientMessagingService.createVariables(user.getId(), message.getRequestId(), null)
                .putValue("requestedFaction", message.getFaction());

        runtimeService.correlateMessage(REQUEST_CHARACTER_MESSAGE, message.getRequestId().toString(), variables);
    }

    @EventListener
    public void onSelectCharacterName(SelectCharacterNameMessage message) {
        log.debug("onSelectCharacterName");

        runtimeService.correlateMessage(RECEIVE_CHARACTER_NAME_SELECTION_MESSAGE, message.getRequestId().toString(),
                Variables.createVariables().putValue("selectedName", message.getName()));
    }
}
