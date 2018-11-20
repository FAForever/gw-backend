package com.faforever.gw;

import com.faforever.gw.model.*;
import com.faforever.gw.model.repository.*;
import com.faforever.gw.services.generator.UniverseGenerator;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DemoDataInitializer {
    private final UniverseGenerator universeGenerator;
    private final CharacterRepository characterRepository;
    private final PlanetRepository planetRepository;
    private final MapRepository mapRepository;
    private final RankRepository rankRepository;
    private final SolarSystemRepository solarSystemRepository;
    private final ReinforcementsRepository reinforcementsRepository;

    @Inject
    public DemoDataInitializer(UniverseGenerator universeGenerator, CharacterRepository characterRepository, PlanetRepository planetRepository, MapRepository mapRepository, RankRepository rankRepository, SolarSystemRepository solarSystemRepository, ReinforcementsRepository reinforcementsRepository) {
        this.universeGenerator = universeGenerator;
        this.characterRepository = characterRepository;
        this.planetRepository = planetRepository;
        this.mapRepository = mapRepository;
        this.rankRepository = rankRepository;
        this.solarSystemRepository = solarSystemRepository;
        this.reinforcementsRepository = reinforcementsRepository;
    }

    @Transactional
    public void run() throws SQLException {
//        Collection<SolarSystem> solarSystems = universeGenerator.generate(100L, 100L, 1L, 300, 5, 1);
//        universeGenerator.persist();

        generateUserToken();

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
        character.getCreditJournalList().add(new CreditJournalEntry(character, null, CreditJournalEntryReason.REGULAR_INCOME, 600.0));
        characterRepository.save(character);

        character = new GwCharacter();
        character.setId(UUID.fromString("a2222222-e35c-11e6-bf01-fe55135034f3"));
        character.setFafId(2);
        character.setName("UEF Bravo");
        character.setFaction(Faction.UEF);
        character.setXp(25000L);
        character.setRank(rank3);
        character.getCreditJournalList().add(new CreditJournalEntry(character, null, CreditJournalEntryReason.REGULAR_INCOME, 1400.0));
        characterRepository.save(character);

        character = new GwCharacter();
        character.setId(UUID.fromString("a3333333-e4e2-11e6-bf01-fe55135034f3"));
        character.setFafId(3);
        character.setName("Cybran Charlie");
        character.setFaction(Faction.CYBRAN);
        character.setXp(0L);
        character.setRank(rank1);
        character.getCreditJournalList().add(new CreditJournalEntry(character, null, CreditJournalEntryReason.REGULAR_INCOME, 300.0));
        characterRepository.save(character);

        character = new GwCharacter();
        character.setId(UUID.fromString("a4444444-e4e2-11e6-bf01-fe55135034f3"));
        character.setFafId(4);
        character.setName("Cybran Delta");
        character.setFaction(Faction.CYBRAN);
        character.setXp(1000L);
        character.setRank(rank2);
        character.getCreditJournalList().add(new CreditJournalEntry(character, null, CreditJournalEntryReason.REGULAR_INCOME, 100.0));
        characterRepository.save(character);

        character = new GwCharacter();
        character.setId(UUID.fromString("a5555555-e4e2-11e6-bf01-fe55135034f3"));
        character.setFafId(5);
        character.setName("Aeon Echo");
        character.setFaction(Faction.AEON);
        character.setXp(900L);
        character.setRank(rank1);
        character.getCreditJournalList().add(new CreditJournalEntry(character, null, CreditJournalEntryReason.REGULAR_INCOME, 200.0));
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


        {
            Unit unit = new Unit("UEL0202", "Pillar", Faction.UEF, TechLevel.T2);
            Reinforcement reinforcement = new Reinforcement(ReinforcementsType.TRANSPORTED_UNITS, unit, null, 5, 300);
            reinforcementsRepository.save(reinforcement);
        }

        Arrays.stream(Faction.values()).forEach(faction -> {
            Unit unit = new Unit(String.format("XXL_%s_0101", faction.getDbKey()), String.format("%s Unit 1", faction.getName()), faction, TechLevel.T1);
            Reinforcement reinforcement = new Reinforcement(ReinforcementsType.TRANSPORTED_UNITS, unit, null, 5, 50);
            reinforcementsRepository.save(reinforcement);

            unit = new Unit(String.format("XXL_%s_0201", faction.getDbKey()), String.format("%s Unit 2", faction.getName()), faction, TechLevel.T2);
            reinforcement = new Reinforcement(ReinforcementsType.TRANSPORTED_UNITS, unit, null, 30, 200);
            reinforcementsRepository.save(reinforcement);
        });
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

    private void generateUserToken() {
        MacSigner macSigner = new MacSigner("secret");
        // {"expires":4102358400, "authorities": [], "user_id": 1, "user_name": "UEF Alpha"}
        // {"expires":4102358400, "authorities": [], "user_id": 2, "user_name": "UEF Bravo"}
        // {"expires":4102358400, "authorities": [], "user_id": 3, "user_name": "Cybran Charlie"}
        // {"expires":4102358400, "authorities": [], "user_id": 4, "user_name": "Cybran Delta"}
        // {"expires":4102358400, "authorities": [], "user_id": 5, "user_name": "Aeon Echo"}

        System.out.println("-1- UEF Alpha");
        Jwt token = JwtHelper.encode("{\"expires\":4102358400, \"authorities\": [], \"user_id\": 1, \"user_name\": \"UEF Alpha\"}", macSigner); // Batto
        System.out.println(token.getEncoded());

        System.out.println("-2- UEF Bravo");
        token = JwtHelper.encode("{\"expires\":4102358400, \"authorities\": [], \"user_id\": 2, \"user_name\": \"UEF Bravo\"}", macSigner);
        System.out.println(token.getEncoded());

        System.out.println("-3- Cybran Charlie");
        token = JwtHelper.encode("{\"expires\":4102358400, \"authorities\": [], \"user_id\": 3, \"user_name\": \"Cybran Charlie\"}", macSigner); // TAG_ROCK
        System.out.println(token.getEncoded());

        System.out.println("-4- Cybran Delta");
        token = JwtHelper.encode("{\"expires\":4102358400, \"authorities\": [], \"user_id\": 4, \"user_name\": \"Cybran Delta\"}", macSigner);
        System.out.println(token.getEncoded());

        System.out.println("-5- Aeon Echo");
        token = JwtHelper.encode("{\"expires\":4102358400, \"authorities\": [], \"user_id\": 5, \"user_name\": \"Aeon Echo\"}", macSigner);
        System.out.println(token.getEncoded());

        System.out.println("-X- Unregistered user");
        token = JwtHelper.encode("{\"expires\":4102358400, \"authorities\": [], \"user_id\": 6, \"user_name\": \"Unregistered user\"}", macSigner);
        System.out.println(token.getEncoded());
    }
}
