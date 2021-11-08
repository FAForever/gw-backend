package com.faforever.gw;

import com.faforever.gw.model.Faction;
import com.faforever.gw.model.Ground;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Map;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.Rank;
import com.faforever.gw.model.SolarSystem;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.MapRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import com.faforever.gw.model.repository.RankRepository;
import com.faforever.gw.model.repository.SolarSystemRepository;
import com.faforever.gw.services.generator.UniverseGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class DemoDataInitializer {
    private final UniverseGenerator universeGenerator;
    private final CharacterRepository characterRepository;
    private final PlanetRepository planetRepository;
    private final MapRepository mapRepository;
    private final RankRepository rankRepository;
    private final SolarSystemRepository solarSystemRepository;

    @Transactional
    public void run() throws SQLException {
//        Collection<SolarSystem> solarSystems = universeGenerator.generate(100L, 100L, 1L, 300, 5, 1);
//        universeGenerator.persist();

        Rank rank1 = new Rank();
        rank1.setLevel(1);
        rank1.setXpMin(0L);
        rank1.setUefTitle("UNoob");
        rank1.setCybranTitle("CNoob");
        rank1.setAeonTitle("ANoob");
        rank1.setSeraphimTitle("SNoob");
        rankRepository.save(rank1);

        Rank rank2 = new Rank();
        rank2.setLevel(2);
        rank2.setXpMin(1000L);
        rank2.setUefTitle("UExperienced");
        rank2.setCybranTitle("CExperienced");
        rank2.setAeonTitle("AExperienced");
        rank2.setSeraphimTitle("SExperienced");
        rankRepository.save(rank2);

        Rank rank3 = new Rank();
        rank3.setLevel(3);
        rank3.setXpMin(10000L);
        rank3.setUefTitle("UPro");
        rank3.setCybranTitle("CPro");
        rank3.setAeonTitle("APro");
        rank3.setSeraphimTitle("SPro");
        rankRepository.save(rank3);

        GwCharacter character = new GwCharacter();
        character.setId(UUID.fromString("a1111111-e35c-11e6-bf01-fe55135034f3"));
        character.setFafId(1);
        character.setName("UEF Alpha");
        character.setFaction(Faction.UEF);
        character.setXp(999L);
        character.setRank(rank1);
        characterRepository.save(character);

        character = new GwCharacter();
        character.setId(UUID.fromString("a2222222-e35c-11e6-bf01-fe55135034f3"));
        character.setFafId(2);
        character.setName("UEF Bravo");
        character.setFaction(Faction.UEF);
        character.setXp(25000L);
        character.setRank(rank3);
        characterRepository.save(character);

        character = new GwCharacter();
        character.setId(UUID.fromString("a3333333-e4e2-11e6-bf01-fe55135034f3"));
        character.setFafId(3);
        character.setName("Cybran Charlie");
        character.setFaction(Faction.CYBRAN);
        character.setXp(0L);
        character.setRank(rank1);
        characterRepository.save(character);

        character = new GwCharacter();
        character.setId(UUID.fromString("a4444444-e4e2-11e6-bf01-fe55135034f3"));
        character.setFafId(4);
        character.setName("Cybran Delta");
        character.setFaction(Faction.CYBRAN);
        character.setXp(1000L);
        character.setRank(rank2);
        characterRepository.save(character);

        character = new GwCharacter();
        character.setId(UUID.fromString("a5555555-e4e2-11e6-bf01-fe55135034f3"));
        character.setFafId(5);
        character.setName("Aeon Echo");
        character.setFaction(Faction.AEON);
        character.setXp(900L);
        character.setRank(rank1);
        characterRepository.save(character);

        Map map = new Map();
        map.setGround(Ground.SOIL);
        map.setFafMapId(2);
        map.setFafMapVersion(2);
        map.setSize(10);
        map.setTotalSlots(2);
        mapRepository.save(map);

        List<SolarSystem> solarSystemList = new ArrayList<>();

        for (Faction faction : Faction.values()) {
            solarSystemList.add(createSolarSystem(faction, map));
        }

        for (SolarSystem from : solarSystemList) {
            for (SolarSystem to : solarSystemList) {
                if (from == to)
                    continue;
                ;

                from.getConnectedSystems().add(to);
            }
        }
    }

    private SolarSystem createSolarSystem(Faction faction, Map map) {
        SolarSystem solarSystem = new SolarSystem();
        solarSystem.setX(ThreadLocalRandom.current().nextLong(0, 100));
        solarSystem.setY(ThreadLocalRandom.current().nextLong(0, 100));
        solarSystem.setZ(0);
        solarSystemRepository.save(solarSystem);

        createPlanet(solarSystem, faction, map, 5);
        createPlanet(solarSystem, faction, map, 10);

        return solarSystem;
    }

    private void createPlanet(SolarSystem solarSystem, Faction faction, Map map, int orbitLevel) {
        Planet planet = new Planet();
        planet.setSolarSystem(solarSystem);
        planet.setGround(Ground.SOIL);
        planet.setHabitable(true);
        planet.setOrbitLevel(orbitLevel);
        planet.setSize(20);

        planet.setMap(map);
        planet.setCurrentOwner(faction);
        planetRepository.save(planet);
    }
}
