package com.faforever.gw.messaging.client;

import com.faforever.gw.messaging.client.outbound.ErrorMessage;
import com.faforever.gw.messaging.client.outbound.HelloMessage;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.service.CharacterService;
import com.faforever.gw.security.GwUserRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class WebSocketInputHandler extends TextWebSocketHandler {
    private final EntityManager entityManager;
    private final WebSocketRegistry webSocketRegistry;
    private final GwUserRegistry gwUserRegistry;
    private final ClientMessagingService clientMessagingService;
    private final ObjectMapper jsonObjectMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final CharacterService characterService;

    @Inject
    public WebSocketInputHandler(EntityManager entityManager, WebSocketRegistry webSocketRegistry, GwUserRegistry gwUserRegistry, ClientMessagingService clientMessagingService, ObjectMapper jsonObjectMapper, ApplicationEventPublisher eventPublisher, CharacterService characterService) {
        this.entityManager = entityManager;
        this.webSocketRegistry = webSocketRegistry;
        this.gwUserRegistry = gwUserRegistry;
        this.clientMessagingService = clientMessagingService;
        this.jsonObjectMapper = jsonObjectMapper;
        this.eventPublisher = eventPublisher;
        this.characterService = characterService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.trace("Incoming websocket message: {}", message.getPayload());
        ClientMessage clientMessage;

        SecurityContextHolder.getContext().setAuthentication((Authentication) session.getPrincipal());

        try {
            try {
                ClientMessageWrapper wrapper = jsonObjectMapper.readValue(message.getPayload(), ClientMessageWrapper.class);
                clientMessage = wrapper.getData();
            } catch (IOException e) {
                log.error("Invalid message envelope. Ignoring message.");
                clientMessagingService.send(session, new ErrorMessage(null,
                        "E_INVALID",
                        "Invalid message envelope. Ignoring message.."));
                return;
            }

            eventPublisher.publishEvent(clientMessage);
        } finally {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
    }

    @Override
    @Transactional
    public void afterConnectionEstablished(WebSocketSession session) {
        log.debug("User `{}` has connected via WebSocket", session.getPrincipal().getName());

        webSocketRegistry.add(session);
        val user = webSocketRegistry.getUser(session);

        UUID characterId = null;
        UUID currentBattleId = null;

        GwCharacter character = user.getActiveCharacter();

        if (character != null) {
            character = entityManager.merge(character);
            characterId = character.getId();
            currentBattleId = character.getCurrentBattle().map(Battle::getId).orElse(null);
        }

        gwUserRegistry.addConnection(user);
        log.debug("Sending HelloMessage (characterId: {}, currentBattleId: {})", characterId, currentBattleId);
        clientMessagingService.sendToUser(new HelloMessage(characterId, currentBattleId), user);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.debug("User `{}` has closed the WebSocket", session.getPrincipal().getName());
        gwUserRegistry.removeConnection(webSocketRegistry.getUser(session));
        webSocketRegistry.remove(session);
    }
}
