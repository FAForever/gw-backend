package com.faforever.gw.services.messaging;

import com.faforever.gw.websocket.WebSocketEnvelope;
import com.faforever.gw.websocket.WebSocketRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.inject.Inject;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class MessagingService {
    private final WebSocketRegistry webSocketRegistry;
    private final ObjectMapper jsonObjectMapper;

    @Inject
    public MessagingService(WebSocketRegistry webSocketRegistry, ObjectMapper jsonObjectMapper) {
        this.webSocketRegistry = webSocketRegistry;
        this.jsonObjectMapper = jsonObjectMapper;
    }

    public void send(OutgoingWebSocketMessage message) {
        MessageType channel = message.getAction();

        switch (channel.getAudience()) {
            case PUBLIC:
                log.trace("Sending public message");
                webSocketRegistry.getSessions()
                        .forEach(session -> send(session, message));
                break;
            case FACTION:
                log.trace("Sending faction message (faction: {})", message.getFaction());
                webSocketRegistry.getSessions().stream()
                        .filter(session -> webSocketRegistry.getUser(session).getActiveCharacter().getFaction() == message.getFaction())
                        .forEach(session -> send(session, message));
                break;
            case PRIVATE:
                message.getRecipients().forEach(user -> {
                    log.trace("Sending private message (user: {})", user.getName());
                    webSocketRegistry.getSession(user).forEach(session -> send(session, message));
                });
        }
    }

    public VariableMap createVariables(UUID requestId, UUID requestCharacter) {
        log.debug("-> set requestId: {}", requestId);
        log.debug("-> set requestCharacter: {}", requestCharacter);

        return Variables.createVariables()
                .putValue("requestId", requestId)
                .putValue("requestCharacter", requestCharacter);
    }

    private TextMessage box(OutgoingWebSocketMessage message) throws JsonProcessingException {
        return new TextMessage(
                jsonObjectMapper.writeValueAsString(
                        new WebSocketEnvelope(message.getAction().getName(), jsonObjectMapper.writeValueAsString(message))
                )
        );
    }

    public void send(WebSocketSession session, OutgoingWebSocketMessage message) {
        try {
            session.sendMessage(box(message));
        } catch (JsonProcessingException e) {
            log.error("Error on converting message to string", e);
        } catch (IOException e) {
            log.error("Sending message to user {0} failed", webSocketRegistry.getUser(session).getName(), e);
        }
    }
}
