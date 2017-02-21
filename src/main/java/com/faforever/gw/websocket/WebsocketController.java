package com.faforever.gw.websocket;

import com.faforever.gw.bpmn.services.PlanetaryAssaultService;
import com.faforever.gw.data.domain.ChatMessage;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import com.faforever.gw.security.User;
import com.faforever.gw.websocket.incoming.InitiateAssaultMessage;
import com.faforever.gw.websocket.incoming.JoinAssaultMessage;
//import jersey.repackaged.com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
public class WebsocketController {
    private final SimpMessagingTemplate template;

    private final PlanetaryAssaultService planetaryAssaultService;

    private final RuntimeService runtimeService;
    private final CharacterRepository characterRepository;
    private final ParticipantRepository participantRepository;

    @Inject
    public WebsocketController(SimpMessagingTemplate template, PlanetaryAssaultService planetaryAssaultService, RuntimeService runtimeService, CharacterRepository characterRepository, PlanetRepository planetRepository, BattleRepository battleRepository, ParticipantRepository participantRepository) {
        this.template = template;
        this.planetaryAssaultService = planetaryAssaultService;
        this.runtimeService = runtimeService;
        this.characterRepository = characterRepository;
        this.participantRepository = participantRepository;
    }

    @MessageMapping("/initiateAssault")
    public void initiateAssault(InitiateAssaultMessage message, User user) throws Exception {
        log.trace("received /initiateAssault, message: {}, user: {}", message, user);
        planetaryAssaultService.characterInitiatesAssault(message, user);
    }

    @MessageMapping("/joinAssault")
    public void joinAssault(JoinAssaultMessage message, User user) throws Exception {
        log.trace("received /joinAssault, message: {}, user: {}", message, user);
        planetaryAssaultService.characterJoinsAssault(message, user);
    }
    @MessageMapping("/leaveAssault")
    public void leaveAssault(JoinAssaultMessage message, User user) throws Exception {
        log.trace("received /joinAssault, message: {}, user: {}", message, user);
        planetaryAssaultService.characterLeavesAssault(message, user);
    }

    @MessageMapping("/test")
    @SendTo("/planets/attacked")
    public Greeting test(InitiateAssaultMessage message) {
        return new Greeting("Hello!");
    }


    @SubscribeMapping("/chat.participants")
    public Collection<LoginEvent> retrieveParticipants() {
        return participantRepository.getActiveSessions().values();
    }



    public void send(String channel, Object payload) {
        template.convertAndSend(channel, payload);
    }

    @MessageMapping("/chat.message")
    public ChatMessage filterMessage(@Payload ChatMessage message, Principal principal) {
        message.setUsername(principal.getName());

        return message;
    }

    @MessageMapping("/chat.private.{username}")
    public void filterPrivateMessage(@Payload ChatMessage message, @DestinationVariable("username") String username, Principal principal) {
        message.setUsername(principal.getName());

        template.convertAndSend("/user/" + username + "/exchange/amq.direct/chat.message", message);
    }
}
