package com.faforever.gw.websocket;

import com.faforever.gw.bpmn.services.PlanetaryAssaultService;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.client.incoming.InitiateAssaultMessage;
import com.faforever.gw.services.messaging.client.incoming.JoinAssaultMessage;
import com.faforever.gw.services.messaging.client.incoming.LeaveAssaultMessage;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Slf4j
@Controller
public class WebSocketController {
    private final PlanetaryAssaultService planetaryAssaultService;

    private final RuntimeService runtimeService;
    private final CharacterRepository characterRepository;

    @Inject
    public WebSocketController(PlanetaryAssaultService planetaryAssaultService, RuntimeService runtimeService, CharacterRepository characterRepository, PlanetRepository planetRepository, BattleRepository battleRepository) {
        this.planetaryAssaultService = planetaryAssaultService;
        this.runtimeService = runtimeService;
        this.characterRepository = characterRepository;
    }

    @ActionMapping("initiateAssault")
    public void initiateAssault(InitiateAssaultMessage message, User user) throws Exception {
        log.trace("received initiateAssault, message: {}, user: {}", message, user);
        planetaryAssaultService.onCharacterInitiatesAssault(message, user);
    }

    @ActionMapping("joinAssault")
    public void joinAssault(JoinAssaultMessage message, User user) throws Exception {
        log.trace("received joinAssault, message: {}, user: {}", message, user);
        planetaryAssaultService.onCharacterJoinsAssault(message, user);
    }

    @ActionMapping("leaveAssault")
    public void leaveAssault(LeaveAssaultMessage message, User user) throws Exception {
        log.trace("received leaveAssault, message: {}, user: {}", message, user);
        planetaryAssaultService.onCharacterLeavesAssault(message, user);
    }

//    @ActionMapping("debug/fakeGameResult")
//    public void fakeGameResult(JoinAssaultMessage message, User user){
//        log.trace("received debug/fakeGameResult, message: {}, user: {}", message, user);
//
//        GameResult gameResult = new GameResult();
//        gameResult.setBattle(message.getBattleId());
//        gameResult.setWinner(Faction.UEF);
//
//        ArrayList<GameCharacterResult> characterResults = new ArrayList<>();
//        characterResults.add(new GameCharacterResult(UUID.fromString("a1111111-e35c-11e6-bf01-fe55135034f3"), BattleParticipantResult.VICTORY, null));
//        characterResults.add(new GameCharacterResult(UUID.fromString("a2222222-e35c-11e6-bf01-fe55135034f3"), BattleParticipantResult.DEATH, UUID.fromString("a4444444-e4e2-11e6-bf01-fe55135034f3")));
//        characterResults.add(new GameCharacterResult(UUID.fromString("a3333333-e4e2-11e6-bf01-fe55135034f3"), BattleParticipantResult.RECALL, null));
//        characterResults.add(new GameCharacterResult(UUID.fromString("a4444444-e4e2-11e6-bf01-fe55135034f3"), BattleParticipantResult.DEATH, UUID.fromString("a1111111-e35c-11e6-bf01-fe55135034f3")));
//        gameResult.setCharacterResults(characterResults);
//
//        planetaryAssaultService.onGameResult(gameResult);
//    }
}
