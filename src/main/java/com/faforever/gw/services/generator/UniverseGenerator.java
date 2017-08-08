package com.faforever.gw.services.generator;

import com.faforever.gw.model.Ground;
import com.faforever.gw.model.Map;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.SolarSystem;
import com.faforever.gw.model.repository.PlanetRepository;
import com.faforever.gw.model.repository.SolarSystemRepository;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UniverseGenerator {
    private final DecisionService decisionService;
    private final SolarSystemNameGenerator solarSystemNameGenerator;
    private final SolarSystemRepository solarSystemRepository;
    private final PlanetRepository planetRepository;
    private ThreadLocalRandom random;

    @Inject
    public UniverseGenerator(DecisionService decisionService, SolarSystemNameGenerator solarSystemNameGenerator, SolarSystemRepository solarSystemRepository, PlanetRepository planetRepository) {
        this.decisionService = decisionService;
        this.solarSystemNameGenerator = solarSystemNameGenerator;
        this.solarSystemRepository = solarSystemRepository;
        this.planetRepository = planetRepository;
    }

    public void persist(Collection<SolarSystem> solarSystems) {

    }


    public Collection<SolarSystem> generate(long width, long height, long depth, int totalSolarSystems, int averagePlanetsPerSolarSystem, int planetCountMaxDeviation) {
        random = ThreadLocalRandom.current();

        List<SolarSystem> solarSystems = new ArrayList<>();

        for (long i = 0; i < totalSolarSystems; i++) {
            SolarSystem solarSystem = new SolarSystem();
            solarSystems.add(solarSystem);

            solarSystem.setX(random.nextLong(width));
            solarSystem.setY(random.nextLong(height));
            solarSystem.setZ(random.nextLong(depth));
            solarSystem.setName(solarSystemNameGenerator.next());

            int planetCount = random.nextInt(averagePlanetsPerSolarSystem - planetCountMaxDeviation, averagePlanetsPerSolarSystem + planetCountMaxDeviation);

            List<Integer> orbitLevels = new ArrayList<>(planetCount);
            for (int j = 0; j < planetCount; j++) {
                orbitLevels.add(random.nextInt(1, 30));
            }
            Collections.sort(orbitLevels);

            List<Planet> planets = new ArrayList<>(planetCount);
            for (int planetIndex = 0; planetIndex < planetCount; planetIndex++) {
                planets.add(createPlanet(solarSystem, planetIndex, orbitLevels.get(planetIndex)));
            }
            solarSystem.setPlanets(planets);

        }

        return solarSystems;
    }

    private Planet createPlanet(SolarSystem solarSystem, int index, int orbitLevel) {
        Planet planet = new Planet();
        planet.setSolarSystem(solarSystem);
        planet.setOrbitLevel(orbitLevel);
        planet.setName(solarSystem.getName() + " " + Util.convertIntToRoman(index + 1));
        planet.setSize(getRandomSize());

        DmnDecisionTableResult planetParameter = decisionService.evaluateDecisionTableByKey("planet_environment",
                Variables.createVariables()
                        .putValue("orbitLevel", orbitLevel)
                        .putValue("randomValue", random.nextInt(1, 100))
        );

        Ground ground = Ground.valueOf(planetParameter.getFirstResult().getEntry("ground"));
        boolean isHabitable = planetParameter.getFirstResult().getEntry("habitable");

        planet.setGround(ground);
        planet.setHabitable(isHabitable);
        return planet;
    }


    private Map chooseRandomFittingMap(Planet planet) {
        return null; // FIXME: implement logic
    }

    /**
     * @return (weighed) random FA map size (5x5, 10x10, 20x20, 40x40)
     */
    private int getRandomSize() {
        int val = random.nextInt(0, 99);

        if (val < 20) {
            return 5;
        } else if (val < 50) {
            return 10;
        } else if (val < 80) {
            return 20;
        } else {
            return 40;
        }
    }
}
