package com.faforever.gw.services.generator;

import com.faforever.gw.model.Faction;
import com.faforever.gw.model.Ground;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.SolarSystem;
import com.faforever.gw.model.repository.PlanetRepository;
import com.faforever.gw.model.repository.SolarSystemRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.variable.Variables;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class UniverseGenerator {
    private final DecisionService decisionService;
    private final SolarSystemNameGenerator solarSystemNameGenerator;
    private final SolarSystemRepository solarSystemRepository;
    private final PlanetRepository planetRepository;
    private final MapSelectorService mapSelectorService;
    private ThreadLocalRandom random;

    private static final double INNER_CIRCLE_DISTANCE = 15L;
    private static final double OUTER_CIRCLE_DISTANCE = 30L;
    private static final int OUTER_CIRCLE_MIN_COUNT = 3;

    private long width;
    private long height;
    private long depth;
    private int totalSolarSystems;
    private List<SolarSystem> solarSystems = new ArrayList<>();
    private int averagePlanetsPerSolarSystem;
    private int planetCountMaxDeviation;

    @Inject
    public UniverseGenerator(DecisionService decisionService, SolarSystemNameGenerator solarSystemNameGenerator, SolarSystemRepository solarSystemRepository, PlanetRepository planetRepository, MapSelectorService mapSelectorService) {
        this.decisionService = decisionService;
        this.solarSystemNameGenerator = solarSystemNameGenerator;
        this.solarSystemRepository = solarSystemRepository;
        this.planetRepository = planetRepository;
        this.mapSelectorService = mapSelectorService;
    }

    @Transactional
    public void persist() {
        for (SolarSystem solarSystem : solarSystems) {
            solarSystemRepository.save(solarSystem);
            planetRepository.saveAll(solarSystem.getPlanets());
        }
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
//        List<Planet> planetsInRandomOrder = new ArrayList<>();
//        solarSystems.forEach(solarSystem -> planetsInRandomOrder.addAll(solarSystem.getPlanets()));
//        Collections.shuffle(planetsInRandomOrder);
//
//        int factionIndex = 0;
//        for (Planet planet : planetsInRandomOrder) {
//            planet.setCurrentOwner(Faction.values()[factionIndex]);
//            factionIndex = (factionIndex + 1) % Faction.values().length;
//        }

        Set<SolarSystem> remainingSystems = new HashSet<>();
        remainingSystems.addAll(solarSystems);

        java.util.Map<Faction, Long> planetCounts = new HashMap<>();
        PriorityQueue<Faction> factionPriorityQueue = new PriorityQueue<>(Comparator.comparingDouble(planetCounts::get));

        java.util.Map<Faction, List<SolarSystem>> candidatesPerFaction = new HashMap<>();
        java.util.Map<Faction, List<SolarSystem>> ignoredPerFaction = new HashMap<>();

        List<SolarSystem> universeCorners = findUniverseCorners();

        for (Faction f : Faction.values()) {
            planetCounts.put(f, 0L);
            factionPriorityQueue.offer(f);
            List<SolarSystem> candidateList = new ArrayList<>();
            candidatesPerFaction.put(f, candidateList);
            ignoredPerFaction.put(f, new ArrayList<>());

            SolarSystem startingLocation = universeCorners.get(random.nextInt(0, universeCorners.size()));
            candidateList.add(startingLocation);
            universeCorners.remove(startingLocation);
        }

        while (remainingSystems.size() > 0) {
            Faction currentFaction = factionPriorityQueue.peek();

            List<SolarSystem> candidates = candidatesPerFaction.get(currentFaction);
            List<SolarSystem> ignored = ignoredPerFaction.get(currentFaction);

            if (candidates.size() == 0) {
                return;
//                throw new NoSuchElementException("Could not find an assignable candidate!");
            }

            SolarSystem next = candidates.get(0);
            ignored.add(next);
            candidates.remove(next);

            next.getConnectedSystems().stream()
                    .filter(solarSystem -> !ignored.contains(solarSystem))
                    .forEach(candidates::add);

            if (!remainingSystems.contains(next)) {
                continue;
            }


            next.getPlanets().forEach(planet -> planet.setCurrentOwner(currentFaction));
            remainingSystems.remove(next);

            planetCounts.put(currentFaction, planetCounts.get(currentFaction) + next.getPlanets().size());
            factionPriorityQueue.offer(factionPriorityQueue.poll()); // update priorities
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

        List<SolarSystem> result = new ArrayList<>();
        result.add(getNearestNeighbor(topLeftFrontReference));
        result.add(getNearestNeighbor(topRightFrontReference));
        result.add(getNearestNeighbor(bottomLeftBehindReference));
        result.add(getNearestNeighbor(bottomRightBehindReference));

        return result;
    }

    private SortedSet<SolarSystem> getNeighborsSortedByDistance(SolarSystem origin) {
        SortedSet<SolarSystem> solarSystemSortedSet = new TreeSet<>(Comparator.comparingDouble(neighbor -> SolarSystem.getDistanceBetween(origin, neighbor)));
        solarSystems.stream()
                .filter(solarSystem -> solarSystem != origin)
                .forEach(solarSystemSortedSet::add);

        return solarSystemSortedSet;
    }

    private SolarSystem getNearestNeighbor(SolarSystem origin) {
        SortedSet<SolarSystem> neighborsSortedByDistance = getNeighborsSortedByDistance(origin);

        if (neighborsSortedByDistance.size() == 0) {
            return null;
        }

        return neighborsSortedByDistance.first();
    }

    @NotNull
    private void createSolarSystems() {
        solarSystems = new ArrayList<>();

        for (long i = 0; i < totalSolarSystems; i++) {
            SolarSystem solarSystem = new SolarSystem();
            solarSystems.add(solarSystem);

            solarSystem.setName(solarSystemNameGenerator.next());
            log.trace(MessageFormat.format("Creating SolarSystem #{0} - name: {1}", i + 1), solarSystem.getName());

            boolean validCoordinates = false;
            while (!validCoordinates) {
                solarSystem.setX(random.nextLong(width));
                solarSystem.setY(random.nextLong(height));
                solarSystem.setZ(random.nextLong(depth));

                SolarSystem nearestNeighbor = getNearestNeighbor(solarSystem);
                if (nearestNeighbor == null) {
                    validCoordinates = true; // it's the first solar system
                } else {
                    log.debug("comparing ({},{}) with ({},{})", solarSystem.getX(), solarSystem.getY(), nearestNeighbor.getX(), nearestNeighbor.getY());
                    validCoordinates = SolarSystem.getDistanceBetween(solarSystem, nearestNeighbor) >= 2;
                }
                log.debug("Set random coordinates to ({},{},{}) => valid: {}", solarSystem.getX(), solarSystem.getY(), solarSystem.getZ(), validCoordinates);
            }

            int planetCount = random.nextInt(averagePlanetsPerSolarSystem - planetCountMaxDeviation, averagePlanetsPerSolarSystem + planetCountMaxDeviation + 1); // +1 because upper bound is not included

            List<Integer> orbitLevels = new ArrayList<>(planetCount);
            for (int j = 0; j < planetCount; j++) {
                orbitLevels.add(random.nextInt(1, 30));
            }
            Collections.sort(orbitLevels);

            log.trace("Generating {0} planets at orbit levels {1}", planetCount, orbitLevels);
            Set<Planet> planets = new HashSet<>(planetCount);
            for (int planetIndex = 0; planetIndex < planetCount; planetIndex++) {
                planets.add(createPlanet(solarSystem, planetIndex, orbitLevels.get(planetIndex)));
            }
            solarSystem.setPlanets(planets);
        }

    }

    private void setUpLinks() {
        // requirement: the result must be a connected graph
        solarSystems.forEach(this::setUpLinks);
    }

    private void setUpLinks(SolarSystem current) {
        // The algorithm uses brute force instead of some fancy geometry algorithms, because we run it just once per season
        // FIXME: the result is not necessary connected

        SortedSet<SolarSystem> neighbors = getNeighborsSortedByDistance(current);

        int counter = 0;

        for (SolarSystem neighbor : neighbors) {
            if (counter >= 3)
                return;

            current.getConnectedSystems().add(neighbor);
            neighbor.getConnectedSystems().add(current);

            counter++;
        }
    }

    private Planet createPlanet(SolarSystem solarSystem, int index, int orbitLevel) {
        Planet planet = new Planet();
        planet.setSolarSystem(solarSystem);
        planet.setOrbitLevel(orbitLevel);
        planet.setName(solarSystem.getName() + " " + Util.convertIntToRoman(index + 1));
        planet.setSize(getRandomSize());
        planet.setMap(mapSelectorService.chooseMapFor(planet));

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
