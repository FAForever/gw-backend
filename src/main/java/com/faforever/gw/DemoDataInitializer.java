package com.faforever.gw;

import com.faforever.gw.model.*;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.MapRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Component
public class DemoDataInitializer {
    private final CharacterRepository characterRepository;
    private final PlanetRepository planetRepository;
    private final MapRepository mapRepository;
    private final BattleRepository battleRepository;

    @Inject
    public DemoDataInitializer(CharacterRepository characterRepository, PlanetRepository planetRepository, MapRepository mapRepository, BattleRepository battleRepository) {
        this.characterRepository = characterRepository;
        this.planetRepository = planetRepository;
        this.mapRepository = mapRepository;
        this.battleRepository = battleRepository;
    }

    @Transactional
    public void run() throws SQLException {
        generateUserTokaen();

        GwCharacter character = new GwCharacter();
        character.setId(UUID.fromString("a1111111-e35c-11e6-bf01-fe55135034f3"));
        character.setFafId(1);
        character.setName("UEF Alpha");
        character.setFaction(Faction.UEF);
        characterRepository.save(character);

        character = new GwCharacter();
        character.setId(UUID.fromString("a2222222-e35c-11e6-bf01-fe55135034f3"));
        character.setFafId(2);
        character.setName("UEF Bravo");
        character.setFaction(Faction.UEF);
        characterRepository.save(character);

        character = new GwCharacter();
        character.setId(UUID.fromString("a3333333-e4e2-11e6-bf01-fe55135034f3"));
        character.setFafId(3);
        character.setName("Cybran Charlie");
        character.setFaction(Faction.CYBRAN);
        characterRepository.save(character);

        character = new GwCharacter();
        character.setId(UUID.fromString("a4444444-e4e2-11e6-bf01-fe55135034f3"));
        character.setFafId(4);
        character.setName("Cybran Delta");
        character.setFaction(Faction.CYBRAN);
        characterRepository.save(character);

        character = new GwCharacter();
        character.setId(UUID.fromString("a5555555-e4e2-11e6-bf01-fe55135034f3"));
        character.setFafId(5);
        character.setName("Aeon Echo");
        character.setFaction(Faction.AEON);
        characterRepository.save(character);

        Map map = new Map();
        map.setGround(Ground.SOIL);
        map.setFafMapId(1);
        map.setFafMapVersion(1);
        map.setSize(10);
        map.setTotalSlots(4);
        mapRepository.save(map);

        Planet planet = new Planet();
        planet.setId(UUID.fromString("e1e4c4c4-e35c-11e6-bf01-fe55135034f3"));
        planet.setGround(Ground.SOIL);
        planet.setHabitable(true);
        planet.setOrbitLevel(5);
        planet.setSize(20);

        planet.setMap(map);
        planetRepository.save(planet);

        Battle initBattle = new Battle();
        initBattle.setId(UUID.randomUUID());
        initBattle.setPlanet(planet);
        initBattle.setInitiatedAt(Timestamp.from(Instant.EPOCH));
        initBattle.setStartedAt(Timestamp.from(Instant.EPOCH));
        initBattle.setEndedAt(Timestamp.from(Instant.EPOCH));
        initBattle.setWinningFaction(Faction.CYBRAN);
        initBattle.setStatus(BattleStatus.FINISHED);

        battleRepository.save(initBattle);
    }

    private void generateUserTokaen() {
        MacSigner macSigner = new MacSigner("secret");
        // {"expires":4102358400, "authorities": [], "user_id": 1, "user_name": "UEF Alpha"}
        // {"expires":4102358400, "authorities": [], "user_id": 2, "user_name": "UEF Bravo"}
        // {"expires":4102358400, "authorities": [], "user_id": 3, "user_name": "Cybran Charlie"}
        // {"expires":4102358400, "authorities": [], "user_id": 4, "user_name": "Cybran Delta"}
        // {"expires":4102358400, "authorities": [], "user_id": 5, "user_name": "Aeon Echo"}

        System.out.println("-1- UEF Alpha");
        Jwt token = JwtHelper.encode("{\"expires\":4102358400, \"authorities\": [], \"user_id\": 1, \"user_name\": \"UEF Alpha\"}", macSigner);
        System.out.println(token.getEncoded());

        System.out.println("-2- UEF Bravo");
        token = JwtHelper.encode("{\"expires\":4102358400, \"authorities\": [], \"user_id\": 2, \"user_name\": \"UEF Bravo\"}", macSigner);
        System.out.println(token.getEncoded());

        System.out.println("-3- Cybran Charlie");
        token = JwtHelper.encode("{\"expires\":4102358400, \"authorities\": [], \"user_id\": 3, \"user_name\": \"Cybran Charlie\"}", macSigner);
        System.out.println(token.getEncoded());

        System.out.println("-4- Cybran Delta");
        token = JwtHelper.encode("{\"expires\":4102358400, \"authorities\": [], \"user_id\": 4, \"user_name\": \"Cybran Delta\"}", macSigner);
        System.out.println(token.getEncoded());

        System.out.println("-5- Aeon Echo");
        token = JwtHelper.encode("{\"expires\":4102358400, \"authorities\": [], \"user_id\": 5, \"user_name\": \"Aeon Echo\"}", macSigner);
        System.out.println(token.getEncoded());
    }
}
