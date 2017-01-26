package com.faforever.gw;

import com.faforever.gw.model.*;
import com.faforever.gw.model.repository.CharacterRepository;
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

    @Inject
    public DemoDataInitializer(CharacterRepository characterRepository, PlanetRepository planetRepository) {
        this.characterRepository = characterRepository;
        this.planetRepository = planetRepository;
    }

    @Transactional
    public void run() throws SQLException {
        org.h2.tools.Server.createWebServer("-web").start();

        GwCharacter character = new GwCharacter();
        character.setId(UUID.fromString("a81dba16-e35c-11e6-bf01-fe55135034f3"));
        character.setName("BattleInitiator");
        character.setFaction(Faction.UEF); // error -> this does not get saved
        characterRepository.save(character);

        Planet planet = new Planet();
        planet.setId(UUID.fromString("e1e4c4c4-e35c-11e6-bf01-fe55135034f3"));
        planet.setGround(Ground.SOIL);
        planet.setHabitable(true);
        planet.setOrbitLevel(5);
        planet.setSize(20);
        planetRepository.save(planet);
    }
}
