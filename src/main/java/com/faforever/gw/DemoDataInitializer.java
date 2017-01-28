package com.faforever.gw;

import com.faforever.gw.model.*;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.MapRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class DemoDataInitializer {
    private final CharacterRepository characterRepository;
    private final PlanetRepository planetRepository;
    private final MapRepository mapRepository;

    @Inject
    public DemoDataInitializer(CharacterRepository characterRepository, PlanetRepository planetRepository, MapRepository mapRepository) {
        this.characterRepository = characterRepository;
        this.planetRepository = planetRepository;
        this.mapRepository = mapRepository;
    }

    @Transactional
    public void run() throws SQLException {
        org.h2.tools.Server.createWebServer("-web").start();

        GwCharacter character = new GwCharacter();
        character.setId(UUID.fromString("a81dba16-e35c-11e6-bf01-fe55135034f3"));
        character.setName("BattleInitiator");
        character.setFaction(Faction.UEF);
        characterRepository.save(character);

        GwCharacter character2 = new GwCharacter();
        character2.setId(UUID.fromString("a2e67506-e4e2-11e6-bf01-fe55135034f3"));
        character2.setName("Defender");
        character2.setFaction(Faction.CYBRAN);
        characterRepository.save(character2);

        Map map = new Map();
        map.setGround(Ground.SOIL);
        map.setFafMapId(1);
        map.setFafMapVersion(1);
        map.setSize(10);
        map.setTotalSlots(2);
        mapRepository.save(map);

        Planet planet = new Planet();
        planet.setId(UUID.fromString("e1e4c4c4-e35c-11e6-bf01-fe55135034f3"));
        planet.setGround(Ground.SOIL);
        planet.setHabitable(true);
        planet.setOrbitLevel(5);
        planet.setSize(20);

        planet.setMap(map);
        planetRepository.save(planet);
    }
}
