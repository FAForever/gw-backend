package com.faforever.gw.websocket;

import com.faforever.gw.model.Battle;
import com.faforever.gw.security.GwUserRegistry;
import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.client.MessageType;
import com.faforever.gw.services.messaging.client.MessagingService;
import com.faforever.gw.services.messaging.client.outgoing.ErrorMessage;
import com.faforever.gw.services.messaging.client.outgoing.HelloMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class WebSocketInputHandler extends TextWebSocketHandler {
    private final EntityManager entityManager;
    private final WebSocketRegistry webSocketRegistry;
    private final GwUserRegistry gwUserRegistry;
    private final WebSocketController webSocketController;
    private final MessagingService messagingService;
    private final ObjectMapper jsonObjectMapper;
    private final Map<String, ActionFunc> actionMapping = new HashMap<>();

    @Inject
    public WebSocketInputHandler(EntityManager entityManager, WebSocketRegistry webSocketRegistry, GwUserRegistry gwUserRegistry, WebSocketController webSocketController, MessagingService messagingService, ObjectMapper jsonObjectMapper) {
        this.entityManager = entityManager;
        this.webSocketRegistry = webSocketRegistry;
        this.gwUserRegistry = gwUserRegistry;
        this.webSocketController = webSocketController;
        this.messagingService = messagingService;
        this.jsonObjectMapper = jsonObjectMapper;
    }

    @PostConstruct
    public void init() {
        Arrays.stream(webSocketController.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(ActionMapping.class))
                .forEach(method -> {
                    val annotation = method.getAnnotation(ActionMapping.class);

                    actionMapping.put(annotation.value(), (WebSocketEnvelope envelope, User user) -> {
                        try {
                            val messageClass = MessageType.getByAction(envelope.getAction());
                            Object message = jsonObjectMapper.readValue(envelope.getData(), messageClass);
                            method.invoke(webSocketController, message, user);
                        } catch (IllegalAccessException | InvocationTargetException | IOException e) {
                            log.error("ActionMapping for `{}` failed", annotation.value(), e);
                        }
                    });
                });
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        WebSocketEnvelope envelope;

        try {
            envelope = jsonObjectMapper.readValue(message.getPayload(), WebSocketEnvelope.class);

            if (envelope.getAction() == null)
                throw new IOException();
        } catch (Exception e) {
            log.error("Invalid message envelope. Ignoring message.");
            messagingService.send(session, new ErrorMessage(null, null,
                    "E_INVALID",
                    "Invalid message envelope. Ignoring message.."));
            return;
        }

        if (actionMapping.containsKey(envelope.getAction())) {
            actionMapping.get(envelope.getAction()).processMessage(envelope, webSocketRegistry.getUser(session));
        } else {
            log.error("Unknown action `{}`. Ignoring message.", envelope.getAction());
            messagingService.send(session, new ErrorMessage(null, null,
                    "E_INVALID",
                    String.format("Unknown action `%s`. Ignoring message.", envelope.getAction())));
        }
    }

    @Override
    @Transactional
    public void afterConnectionEstablished(WebSocketSession session) {
        log.debug("User `{}` has connected via WebSocket", session.getPrincipal().getName());

        webSocketRegistry.add(session);
        val user = webSocketRegistry.getUser(session);
        val character = entityManager.merge(user.getActiveCharacter());

        gwUserRegistry.addConnection(user);

        val currentBattleId = character.getCurrentBattle().map(Battle::getId).orElse(null);
        messagingService.send(new HelloMessage(user, character.getId(), currentBattleId));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.debug("User `{}` has closed the WebSocket", session.getPrincipal().getName());
        gwUserRegistry.removeConnection(webSocketRegistry.getUser(session));
        webSocketRegistry.remove(session);
    }

    private interface ActionFunc {
        void processMessage(WebSocketEnvelope envelope, User user);
    }
}
