package com.faforever.gw.model;

import com.faforever.gw.bpmn.services.GwErrorService;
import com.faforever.gw.bpmn.services.GwErrorType;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidationHelperTest {
    ValidationHelper validationHelper;

    @Mock
    GwErrorService gwErrorService;

    @Before
    public void setUp() throws Exception {
        when(gwErrorService.getBpmnErrorOf(any(GwErrorType.class))).thenReturn(new BpmnError("test"));

        validationHelper = new ValidationHelper(gwErrorService);
    }

    @Test
    public void validateCharacterInBattle__ExpectTrue_Success() {
        GwCharacter character = mock(GwCharacter.class);
        BattleParticipant participant = mock(BattleParticipant.class);
        when(participant.getCharacter()).thenReturn(character);

        Battle battle = mock(Battle.class);
        List<BattleParticipant> battleParticipantList = new ArrayList<>();
        battleParticipantList.add(participant);

        when(battle.getParticipants()).thenReturn(battleParticipantList);

        validationHelper.validateCharacterInBattle(character, battle, true);
    }

    @Test(expected = BpmnError.class)
    public void validateCharacterInBattle__ExpectTrue_Error() {
        GwCharacter character = mock(GwCharacter.class);

        Battle battle = mock(Battle.class);
        List<BattleParticipant> battleParticipantList = new ArrayList<>();

        when(battle.getParticipants()).thenReturn(battleParticipantList);

        validationHelper.validateCharacterInBattle(character, battle, true);
    }

    @Test
    public void validateCharacterInBattle__ExpectFalse_Success() {
        GwCharacter character = mock(GwCharacter.class);
        BattleParticipant participant = mock(BattleParticipant.class);
        when(participant.getCharacter()).thenReturn(character);

        Battle battle = mock(Battle.class);
        List<BattleParticipant> battleParticipantList = new ArrayList<>();
        battleParticipantList.add(participant);

        when(battle.getParticipants()).thenReturn(battleParticipantList);

        validationHelper.validateCharacterInBattle(mock(GwCharacter.class), battle, false);
    }

    @Test(expected = BpmnError.class)
    public void validateCharacterInBattle__ExpectFalse_Error() {
        GwCharacter character = mock(GwCharacter.class);
        BattleParticipant participant = mock(BattleParticipant.class);
        when(participant.getCharacter()).thenReturn(character);

        Battle battle = mock(Battle.class);
        List<BattleParticipant> battleParticipantList = new ArrayList<>();
        battleParticipantList.add(participant);

        when(battle.getParticipants()).thenReturn(battleParticipantList);

        validationHelper.validateCharacterInBattle(character, battle, false);
    }

    @Test
    public void validateCharacterFreeForGame_EmptyList() {
        GwCharacter character = mock(GwCharacter.class);

        List<BattleParticipant> battleParticipantList = new ArrayList<>();
        when(character.getBattleParticipantList()).thenReturn(battleParticipantList);

        validationHelper.validateCharacterFreeForGame(character);
    }

    @Test(expected = BpmnError.class)
    public void validateCharacterFreeForGame_BattleInitiated() {
        GwCharacter character = mock(GwCharacter.class);
        BattleParticipant participant = mock(BattleParticipant.class);
        Battle battle = mock(Battle.class);
        when(participant.getBattle()).thenReturn(battle);
        when(battle.getStatus()).thenReturn(BattleStatus.INITIATED);

        List<BattleParticipant> battleParticipantList = new ArrayList<>();
        battleParticipantList.add(participant);
        when(character.getBattleParticipantList()).thenReturn(battleParticipantList);

        validationHelper.validateCharacterFreeForGame(character);
    }

    @Test(expected = BpmnError.class)
    public void validateCharacterFreeForGame_BattleRunning() {
        GwCharacter character = mock(GwCharacter.class);
        BattleParticipant participant = mock(BattleParticipant.class);
        Battle battle = mock(Battle.class);
        when(participant.getBattle()).thenReturn(battle);
        when(battle.getStatus()).thenReturn(BattleStatus.RUNNING);

        List<BattleParticipant> battleParticipantList = new ArrayList<>();
        battleParticipantList.add(participant);
        when(character.getBattleParticipantList()).thenReturn(battleParticipantList);

        validationHelper.validateCharacterFreeForGame(character);
    }

    @Test
    public void validateCharacterFreeForGame_BattleFinished() {
        GwCharacter character = mock(GwCharacter.class);
        BattleParticipant participant = mock(BattleParticipant.class);
        Battle battle = mock(Battle.class);
        when(participant.getBattle()).thenReturn(battle);
        when(battle.getStatus()).thenReturn(BattleStatus.FINISHED);

        List<BattleParticipant> battleParticipantList = new ArrayList<>();
        battleParticipantList.add(participant);
        when(character.getBattleParticipantList()).thenReturn(battleParticipantList);

        validationHelper.validateCharacterFreeForGame(character);
    }

    @Test
    public void validateCharacterFreeForGame_BattleCanceled() {
        GwCharacter character = mock(GwCharacter.class);
        BattleParticipant participant = mock(BattleParticipant.class);
        Battle battle = mock(Battle.class);
        when(participant.getBattle()).thenReturn(battle);
        when(battle.getStatus()).thenReturn(BattleStatus.CANCELED);

        List<BattleParticipant> battleParticipantList = new ArrayList<>();
        battleParticipantList.add(participant);
        when(character.getBattleParticipantList()).thenReturn(battleParticipantList);

        validationHelper.validateCharacterFreeForGame(character);
    }

    @Test
    public void validateOpenSlotForCharacter_Attacker_OpenSlot() {
        Battle battle = mock(Battle.class);
        Planet planet = mock(Planet.class);
        Map map = mock(Map.class);
        when(battle.getPlanet()).thenReturn(planet);
        when(planet.getMap()).thenReturn(map);
        when(map.getTotalSlots()).thenReturn(2);

        List<BattleParticipant> battleParticipants = new ArrayList<>();
        BattleParticipant participant = mock(BattleParticipant.class);
        when(participant.getRole()).thenReturn(BattleRole.DEFENDER);
        battleParticipants.add(participant);
        when(battle.getParticipants()).thenReturn(battleParticipants);

        validationHelper.validateOpenSlotForCharacter(battle, BattleRole.ATTACKER);
    }

    @Test(expected = BpmnError.class)
    public void validateOpenSlotForCharacter_Attacker_NoSlot() {
        Battle battle = mock(Battle.class);
        Planet planet = mock(Planet.class);
        Map map = mock(Map.class);
        when(battle.getPlanet()).thenReturn(planet);
        when(planet.getMap()).thenReturn(map);
        when(map.getTotalSlots()).thenReturn(2);

        List<BattleParticipant> battleParticipants = new ArrayList<>();
        BattleParticipant participant = mock(BattleParticipant.class);
        when(participant.getRole()).thenReturn(BattleRole.ATTACKER);
        battleParticipants.add(participant);
        when(battle.getParticipants()).thenReturn(battleParticipants);

        validationHelper.validateOpenSlotForCharacter(battle, BattleRole.ATTACKER);
    }

    @Test
    public void validateOpenSlotForCharacter_Defender_OpenSlot() {
        Battle battle = mock(Battle.class);
        Planet planet = mock(Planet.class);
        Map map = mock(Map.class);
        when(battle.getPlanet()).thenReturn(planet);
        when(planet.getMap()).thenReturn(map);
        when(map.getTotalSlots()).thenReturn(2);

        List<BattleParticipant> battleParticipants = new ArrayList<>();
        BattleParticipant participant = mock(BattleParticipant.class);
        when(participant.getRole()).thenReturn(BattleRole.ATTACKER);
        battleParticipants.add(participant);
        when(battle.getParticipants()).thenReturn(battleParticipants);

        validationHelper.validateOpenSlotForCharacter(battle, BattleRole.DEFENDER);
    }

    @Test(expected = BpmnError.class)
    public void validateOpenSlotForCharacter_Defender_NoSlot() {
        Battle battle = mock(Battle.class);
        Planet planet = mock(Planet.class);
        Map map = mock(Map.class);
        when(battle.getPlanet()).thenReturn(planet);
        when(planet.getMap()).thenReturn(map);
        when(map.getTotalSlots()).thenReturn(2);

        List<BattleParticipant> battleParticipants = new ArrayList<>();
        BattleParticipant participant = mock(BattleParticipant.class);
        when(participant.getRole()).thenReturn(BattleRole.DEFENDER);
        battleParticipants.add(participant);
        when(battle.getParticipants()).thenReturn(battleParticipants);

        validationHelper.validateOpenSlotForCharacter(battle, BattleRole.DEFENDER);
    }

    //    @Test
    public void generateTestUser() {
        MacSigner macSigner = new MacSigner("secret");

        // {"user_id": 1, "user_name": "UEF Alpha", "authorities":["ROLE_USER"], "exp": 4102444740}
        // {"user_id": 2, "user_name": "UEF Bravo", "authorities":["ROLE_USER"], "exp": 4102444740}
        // {"user_id": 3, "user_name": "Cybran Charlie", "authorities":["ROLE_USER"], "exp": 4102444740}
        String tokenData = "{\"user_id\": 2, \"user_name\": \"UEF Bravo\", \"authorities\":[\"ROLE_USER\"], \"exp\": 4102444740}";
        Jwt token = JwtHelper.encode(tokenData, macSigner);
        String stringToken = token.getEncoded();
        System.out.println(stringToken);
        ;
//    public static User getUserFromJwtToken(String stringToken) {
//        try {
//            Jwt token = JwtHelper.decodeAndVerify(stringToken, new MacSigner("secret"));
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode data = objectMapper.readTree(token.getClaims());
//
//            List<String> authorities = objectMapper.readerFor(new TypeReference<List<String>>(){}).readValue(data.get("authorities"));
//            List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
//
//            authorities.forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role)));
//
//            return new User(data.get("user_id").asLong(), data.get("user_name").asText(), stringToken, grantedAuthorities);
//        }
//        catch(Exception e) {
//            throw new RuntimeException("user not authorized");
//        }
    }

    @Test
    public void validateAssaultOnPlanet_enemyFaction() {

        GwCharacter character = mock(GwCharacter.class);
        when(character.getFaction()).thenReturn(Faction.AEON);

        Planet planet = mock(Planet.class);
        SolarSystem solarSystem = mock(SolarSystem.class);

        when(solarSystem.isReachable(Faction.AEON)).thenReturn(true);
        when(planet.getCurrentOwner()).thenReturn(Faction.CYBRAN);

        when(planet.getSolarSystem()).thenReturn(solarSystem);

        validationHelper.validateAssaultOnPlanet(character, planet);

    }

    @Test(expected = BpmnError.class)
    public void validateAssaultOnPlanet_ownFaction() {

        GwCharacter character = mock(GwCharacter.class);
        when(character.getFaction()).thenReturn(Faction.AEON);

        SolarSystem solarSystem = mock(SolarSystem.class);
        when(solarSystem.isReachable(Faction.AEON)).thenReturn(true);

        Planet planet = mock(Planet.class);
        when(planet.getCurrentOwner()).thenReturn(Faction.AEON);
        when(planet.getSolarSystem()).thenReturn(solarSystem);

        validationHelper.validateAssaultOnPlanet(character, planet);

    }

    @Test(expected = BpmnError.class)
    public void validateAssaultOnPlanet_unreachableSolarSystem() {

        GwCharacter character = mock(GwCharacter.class);
        when(character.getFaction()).thenReturn(Faction.AEON);

        SolarSystem system = mock(SolarSystem.class);
        when(system.isReachable(Faction.AEON)).thenReturn(false);

        Planet planet = mock(Planet.class);
        when(planet.getSolarSystem()).thenReturn(system);
        when(planet.getCurrentOwner()).thenReturn(Faction.CYBRAN);

        validationHelper.validateAssaultOnPlanet(character, planet);

    }

    @Test
    public void validateAssaultOnPlanet_reachableSolarSystem() {

        GwCharacter character = mock(GwCharacter.class);
        when(character.getFaction()).thenReturn(Faction.AEON);

        SolarSystem system = mock(SolarSystem.class);
        when(system.isReachable(Faction.AEON)).thenReturn(true);

        Planet planet = mock(Planet.class);
        when(planet.getSolarSystem()).thenReturn(system);
        when(planet.getCurrentOwner()).thenReturn(Faction.CYBRAN);

        validationHelper.validateAssaultOnPlanet(character, planet);

    }

}
