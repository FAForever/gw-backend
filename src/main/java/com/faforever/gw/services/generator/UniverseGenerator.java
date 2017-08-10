package com.faforever.gw.services.generator;

import com.faforever.gw.model.*;
import com.faforever.gw.model.Map;
import com.faforever.gw.model.repository.PlanetRepository;
import com.faforever.gw.model.repository.SolarSystemRepository;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.variable.Variables;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UniverseGenerator {
    private final DecisionService decisionService;
    private final SolarSystemNameGenerator solarSystemNameGenerator;
    private final SolarSystemRepository solarSystemRepository;
    private final PlanetRepository planetRepository;
    private ThreadLocalRandom random;

    private static final double INNER_CIRCLE_DISTANCE = 15L;
    private static final double OUTER_CIRCLE_DISTANCE = 30L;
    private static final int OUTER_CIRCLE_MIN_COUNT = 3;

    private long width;
    private long height;
    private long depth;
    private int totalSolarSystems;
    private List<SolarSystem> solarSystems;
    private int averagePlanetsPerSolarSystem;
    private int planetCountMaxDeviation;

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

        this.width = width;
        this.height = height;
        this.depth = depth;
        this.totalSolarSystems = totalSolarSystems;
        this.averagePlanetsPerSolarSystem = averagePlanetsPerSolarSystem;
        this.planetCountMaxDeviation = planetCountMaxDeviation;

        createSolarSystems();
        setUpLinks();
        populateFactions();

        return solarSystems;
    }

    private void populateFactions() {
        List<Planet> planetsInRandomOrder = new ArrayList<>();
        solarSystems.forEach(solarSystem -> planetsInRandomOrder.addAll(solarSystem.getPlanets()));
        Collections.shuffle(planetsInRandomOrder);

        int factionIndex = 0;
        for (Planet planet : planetsInRandomOrder) {
            planet.setCurrentOwner(Faction.values()[factionIndex]);
            factionIndex = (factionIndex + 1) % Faction.values().length;
        }
    }

    // deprecated
    private List<SolarSystem> findUniverseCorners() {
        // yes, the universe is actually 3d, but we need 4 simple starts points for 4 factions
        SolarSystem topLeftFrontReference = new SolarSystem();
        topLeftFrontReference.setX(0);
        topLeftFrontReference.setY(0);
        topLeftFrontReference.setZ(0);

        SolarSystem topRightFrontReference = new SolarSystem();
        topRightFrontReference.setX(width);
        topRightFrontReference.setY(0);
        topRightFrontReference.setZ(0);

        SolarSystem bottomLeftBehindReference = new SolarSystem();
        bottomLeftBehindReference.setX(0);
        bottomLeftBehindReference.setY(height);
        bottomLeftBehindReference.setZ(depth);

        SolarSystem bottomRightBehindReference = new SolarSystem();
        bottomRightBehindReference.setX(width);
        bottomRightBehindReference.setY(height);
        bottomRightBehindReference.setZ(depth);

        return Arrays.asList(
                getNearestNeighbor(topLeftFrontReference),
                getNearestNeighbor(topRightFrontReference),
                getNearestNeighbor(bottomLeftBehindReference),
                getNearestNeighbor(bottomRightBehindReference)
        );
    }

    private SolarSystem getNearestNeighbor(SolarSystem origin) {
        double shortestDistance = Long.MAX_VALUE;
        SolarSystem nearest = null;

        for (SolarSystem solarSystem : solarSystems) {
            if (solarSystem == origin)
                continue;

            double distance = SolarSystem.getDistanceBetween(origin, solarSystem);

            if (distance < shortestDistance)
                nearest = solarSystem;
        }

        return nearest;
    }

    @NotNull
    private void createSolarSystems() {
        solarSystems = new ArrayList<>();

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
    }

    private void setUpLinks() {
        // requirement: the result must be a connected graph
        solarSystems.forEach(current -> setUpLinks(current));
    }

    private void setUpLinks(SolarSystem current) {
        // The algorithm uses brute force instead of some fancy geometry algorithms, because we run it just once per season
        // FIXME: the result is not necessary connected
        List<SolarSystem> innerCircle = new ArrayList<>();
        List<SolarSystem> outerCircle = new ArrayList<>();

        double shortestDistance = Long.MAX_VALUE;
        SolarSystem nearestSystem = null;

        for (SolarSystem possibleNeighbor : solarSystems) {
            if (current == possibleNeighbor)
                continue;

            double distanceToCurrent = SolarSystem.getDistanceBetween(current, possibleNeighbor);

            if (distanceToCurrent < shortestDistance) {
                shortestDistance = distanceToCurrent;
                nearestSystem = possibleNeighbor;
            }

            if (distanceToCurrent <= INNER_CIRCLE_DISTANCE)
                innerCircle.add(possibleNeighbor);

            if (distanceToCurrent <= OUTER_CIRCLE_DISTANCE)
                outerCircle.add(possibleNeighbor);
        }

        if (innerCircle.size() < OUTER_CIRCLE_MIN_COUNT) {
            // if there are not enough planets in the inner circle, we use an "enhanced" quantum gateway and include the outer circle
            innerCircle.addAll(outerCircle);
            innerCircle.forEach(solarSystem -> {
                QuantumGateLink quantumGateLink = new QuantumGateLink();
                quantumGateLink.setOrigin(current);
                quantumGateLink.setDestination(solarSystem);
                current.getOutgoingLinks().add(quantumGateLink);
                solarSystem.getIncomingLinks().add(quantumGateLink);
            });
        }

        if (innerCircle.size() == 0) {
            // if still no planet in reach, we bidirectional connect to the nearest planet, wherever it is
            QuantumGateLink outgoingLink = new QuantumGateLink();
            outgoingLink.setOrigin(current);
            outgoingLink.setDestination(nearestSystem);
            current.getOutgoingLinks().add(outgoingLink);
            nearestSystem.getIncomingLinks().add(outgoingLink);

            QuantumGateLink incomingLink = new QuantumGateLink();
            incomingLink.setOrigin(nearestSystem);
            incomingLink.setDestination(current);
            nearestSystem.getOutgoingLinks().add(incomingLink);
            current.getIncomingLinks().add(incomingLink);
        }
    }

    private Planet createPlanet(SolarSystem solarSystem, int index, int orbitLevel) {
        Planet planet = new Planet();
        planet.setSolarSystem(solarSystem);
        planet.setOrbitLevel(orbitLevel);
        planet.setName(solarSystem.getName() + " " + Util.convertIntToRoman(index + 1));
        planet.setSize(getRandomSize());
        planet.setMap(chooseRandomFittingMap(planet));

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
