package com.faforever.gw.websocket;

import com.faforever.gw.bpmn.services.CharacterCreationService;
import com.faforever.gw.bpmn.services.PlanetaryAssaultService;
import com.faforever.gw.messaging.client.inbound.*;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import com.faforever.gw.security.User;
import com.faforever.gw.services.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Slf4j
@Controller
public class WebSocketController {
    private final PlanetaryAssaultService planetaryAssaultService;
    private final CharacterCreationService characterCreationService;
    private final AdminService adminService;

    private final RuntimeService runtimeService;
    private final CharacterRepository characterRepository;

    @Inject
    public WebSocketController(PlanetaryAssaultService planetaryAssaultService, RuntimeService runtimeService, CharacterRepository characterRepository, PlanetRepository planetRepository, BattleRepository battleRepository, CharacterCreationService characterCreationService, AdminService adminService) {
        this.planetaryAssaultService = planetaryAssaultService;
        this.runtimeService = runtimeService;
        this.characterRepository = characterRepository;
        this.characterCreationService = characterCreationService;
        this.adminService = adminService;
    }

    @ActionMapping(InitiateAssaultMessage.class)
    public void initiateAssault(InitiateAssaultMessage message, User user) throws Exception {
        log.trace("received initiateAssault, message: {}, user: {}", message, user);
        planetaryAssaultService.onCharacterInitiatesAssault(message, user);
    }

    @ActionMapping(JoinAssaultMessage.class)
    public void joinAssault(JoinAssaultMessage message, User user) throws Exception {
        log.trace("received joinAssault, message: {}, user: {}", message, user);
        planetaryAssaultService.onCharacterJoinsAssault(message, user);
    }

    @ActionMapping(LeaveAssaultMessage.class)
    public void leaveAssault(LeaveAssaultMessage message, User user) throws Exception {
        log.trace("received leaveAssault, message: {}, user: {}", message, user);
        planetaryAssaultService.onCharacterLeavesAssault(message, user);
    }

    @ActionMapping(RequestCharacterMessage.class)
    public void requestCharacter(RequestCharacterMessage message, User user) throws Exception {
        log.trace("received requestCharacter, message: {}, user: {}", message, user);
        characterCreationService.onRequestCharacter(message, user);
    }

    @ActionMapping(SelectCharacterNameMessage.class)
    public void selectCharacterName(SelectCharacterNameMessage message, User user) throws Exception {
        log.trace("received selectCharacterName, message: {}, user: {}", message, user);
        characterCreationService.onSelectCharacterName(message, user);
    }

    @ActionMapping(LinkSolarSystemsRequestMessage.class)
    public void linkSolarSystemsRequest(LinkSolarSystemsRequestMessage message, User user) throws Exception {
        log.trace("received linkSolarSystemsRequest, message: {}, user: {}", message, user);
        adminService.onAddSolarSystemLink(message, user);
    }

    @ActionMapping(UnlinkSolarSystemsRequestMessage.class)
    public void unlinkSolarSystemsRequest(UnlinkSolarSystemsRequestMessage message, User user) throws Exception {
        log.trace("received linkSolarSystemsRequest, message: {}, user: {}", message, user);
        adminService.onRemoveSolarSystemLink(message, user);
    }

    @ActionMapping(SetPlanetFactionRequestMessage.class)
    public void setPlanetFactionRequest(SetPlanetFactionRequestMessage message, User user) throws Exception {
        log.trace("received setPlanetFactionRequest, message: {}, user: {}", message, user);
        adminService.onSetPlanetFaction(message, user);
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
//        characterResults.add(new GameCharacterResult(UUID.fromDbString("a1111111-e35c-11e6-bf01-fe55135034f3"), BattleParticipantResult.VICTORY, null));
//        characterResults.add(new GameCharacterResult(UUID.fromDbString("a2222222-e35c-11e6-bf01-fe55135034f3"), BattleParticipantResult.DEATH, UUID.fromDbString("a4444444-e4e2-11e6-bf01-fe55135034f3")));
//        characterResults.add(new GameCharacterResult(UUID.fromDbString("a3333333-e4e2-11e6-bf01-fe55135034f3"), BattleParticipantResult.RECALL, null));
//        characterResults.add(new GameCharacterResult(UUID.fromDbString("a4444444-e4e2-11e6-bf01-fe55135034f3"), BattleParticipantResult.DEATH, UUID.fromDbString("a1111111-e35c-11e6-bf01-fe55135034f3")));
//        gameResult.setCharacterResults(characterResults);
//
//        planetaryAssaultService.onGameResult(gameResult);
//    }
}
