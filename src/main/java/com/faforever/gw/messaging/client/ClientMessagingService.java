package com.faforever.gw.messaging.client;

import com.faforever.gw.messaging.client.outbound.OutboundClientMessage;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.security.User;
import com.faforever.gw.websocket.WebSocketRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.inject.Inject;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.UUID;

@Slf4j
@Service
public class ClientMessagingService {
    private final WebSocketRegistry webSocketRegistry;
    private final ObjectMapper jsonObjectMapper;
    private final CharacterRepository characterRepository;

    @Inject
    public ClientMessagingService(WebSocketRegistry webSocketRegistry, ObjectMapper jsonObjectMapper, CharacterRepository characterRepository) {
        this.webSocketRegistry = webSocketRegistry;
        this.jsonObjectMapper = jsonObjectMapper;
        this.characterRepository = characterRepository;
    }

    public void sendToPublic(OutboundClientMessage message) {
        Assert.state(message.getAudience() == Audience.PUBLIC, MessageFormat.format("Message class ''{0}'' can't be send to public.", message.getClass()));

        log.trace("Sending public message");
        webSocketRegistry.getSessions()
                .forEach(session -> send(session, message));
    }

    public void sendToUser(OutboundClientMessage message, User user) {
        Assert.state(message.getAudience() == Audience.PRIVATE, MessageFormat.format("Message class ''{0}'' can't be sent to private recipients.", message.getClass()));

        webSocketRegistry.getSession(user.getId())
                .forEach(session -> send(session, message));
    }

//    public void sendToFaction(OutboundClientMessage message, Faction faction) {
//        Assert.state(message.getAudience() == Audience.FACTION, MessageFormat.format("Message class ''{0}'' can't be sent to faction.", message.getClass()));
//
//        log.trace("Sending faction message (faction: {})", faction);
//        webSocketRegistry.getSessions().stream()
//                .filter(session -> {
//                    long userId = webSocketRegistry.getUser(session).getUserId();
//                    return characterService.getFactionByFafId(userId) == faction;
//                })
//                .forEach(session -> send(session, message));
//    }

    public void sendToCharacter(OutboundClientMessage message, UUID characterId) {
        Assert.state(message.getAudience() == Audience.PRIVATE, MessageFormat.format("Message class ''{0}'' can't be sent to private recipients.", message.getClass()));

        GwCharacter character = characterRepository.getOne(characterId);
        webSocketRegistry.getSession(character.getFafId())
                .forEach(session -> send(session, message));
    }

    public VariableMap createVariables(long requestFafUser, UUID requestId, UUID requestCharacter) {
        log.debug("-> set requestFafUser: {}", requestFafUser);
        log.debug("-> set requestId: {}", requestId);
        log.debug("-> set requestCharacter: {}", requestCharacter);

        return Variables.createVariables()
                .putValue("requestFafUser", requestFafUser)
                .putValue("requestId", requestId)
                .putValue("requestCharacter", requestCharacter);
    }

    private TextMessage box(OutboundClientMessage message) throws JsonProcessingException {
        return new TextMessage(jsonObjectMapper.writeValueAsString(new ClientMessageWrapper(message)));
    }

    public void send(WebSocketSession session, OutboundClientMessage message) {
        try {
            session.sendMessage(box(message));
        } catch (JsonProcessingException e) {
            log.error("Error on converting message to string", e);
        } catch (IOException e) {
            log.error("Sending message to user {0} failed", webSocketRegistry.getUser(session).getName(), e);
        }
    }
}
