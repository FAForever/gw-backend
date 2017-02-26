package com.faforever.gw.websocket;

import com.faforever.gw.bpmn.services.PlanetaryAssaultService;
import com.faforever.gw.data.domain.ChatMessage;
import com.faforever.gw.model.BattleParticipantResult;
import com.faforever.gw.model.BattleRole;
import com.faforever.gw.model.GameCharacterResult;
import com.faforever.gw.model.GameResult;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import com.faforever.gw.security.User;
import com.faforever.gw.websocket.incoming.InitiateAssaultMessage;
import com.faforever.gw.websocket.incoming.JoinAssaultMessage;
//import jersey.repackaged.com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.security.Principal;
import java.util.*;

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
        planetaryAssaultService.onCharacterInitiatesAssault(message, user);
    }

    @MessageMapping("/joinAssault")
    public void joinAssault(JoinAssaultMessage message, User user) throws Exception {
        log.trace("received /joinAssault, message: {}, user: {}", message, user);
        planetaryAssaultService.onCharacterJoinsAssault(message, user);
    }

    @MessageMapping("/leaveAssault")
    public void leaveAssault(JoinAssaultMessage message, User user) throws Exception {
        log.trace("received /joinAssault, message: {}, user: {}", message, user);
        planetaryAssaultService.onCharacterLeavesAssault(message, user);
    }

    @MessageMapping("/debug/fakeGameResult")
    public void fakeGameResult(JoinAssaultMessage message, User user){
        log.trace("received /debug/fakeGameResult, message: {}, user: {}", message, user);

        GameResult gameResult = new GameResult();
        gameResult.setBattle(message.getBattleId());

        Map<UUID, GameCharacterResult> characterResults = new HashMap();
        characterResults.put(UUID.fromString("a1111111-e35c-11e6-bf01-fe55135034f3"),new GameCharacterResult(UUID.fromString("a1111111-e35c-11e6-bf01-fe55135034f3"), BattleRole.ATTACKER, BattleParticipantResult.VICTORY));
        characterResults.put(UUID.fromString("a2222222-e35c-11e6-bf01-fe55135034f3"),new GameCharacterResult(UUID.fromString("a2222222-e35c-11e6-bf01-fe55135034f3"), BattleRole.ATTACKER, BattleParticipantResult.DEATH));
        characterResults.put(UUID.fromString("a3333333-e4e2-11e6-bf01-fe55135034f3"),new GameCharacterResult(UUID.fromString("a3333333-e4e2-11e6-bf01-fe55135034f3"), BattleRole.DEFENDER, BattleParticipantResult.RECALL));
        characterResults.put(UUID.fromString("a4444444-e4e2-11e6-bf01-fe55135034f3"),new GameCharacterResult(UUID.fromString("a4444444-e4e2-11e6-bf01-fe55135034f3"), BattleRole.DEFENDER, BattleParticipantResult.DEATH));
        gameResult.setCharacterResults(characterResults);

        Collection<Pair<UUID,UUID>> killList = new ArrayList<>();
        killList.add(new Pair<UUID, UUID>(UUID.fromString("a1111111-e35c-11e6-bf01-fe55135034f3"),UUID.fromString("a4444444-e4e2-11e6-bf01-fe55135034f3")));
        gameResult.setCharacterKills(killList);

        planetaryAssaultService.onGameResult(gameResult);
    }

    @MessageMapping("/test")
    @SendToUser("/direct/error")
    public Greeting test(InitiateAssaultMessage message) {
        return new Greeting("Hello!");
    }


    @SubscribeMapping("/chat.participants")
    public Collection<LoginEvent> retrieveParticipants() {
        return participantRepository.getActiveSessions().values();
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
