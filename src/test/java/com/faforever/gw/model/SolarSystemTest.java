package com.faforever.gw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class SolarSystemTest {

    SolarSystem system, connectedSystem;
    Planet planetA, planetB, planetC, planetConnectedSystem;

    @BeforeEach
    public void init() {
        system = new SolarSystem(); connectedSystem = new SolarSystem();
        planetA = new Planet(); planetB = new Planet(); planetC = new Planet(); planetConnectedSystem = new Planet();
        HashSet<Planet> planets = new HashSet<Planet>(3);
        planets.add(planetA);
        planets.add(planetB);
        planets.add(planetC);
        system.setPlanets(planets);

        HashSet<Planet> planetsConnectedSystem = new HashSet<Planet>();
        planetsConnectedSystem.add(planetConnectedSystem);
        connectedSystem.setPlanets(planetsConnectedSystem);

        HashSet<SolarSystem> connectedSystems = new HashSet<SolarSystem>();
        connectedSystems.add(connectedSystem);
        system.setConnectedSystems(connectedSystems);
    }

    @Test
    public void uniqueOwnerTest_allPlanetsFactionless() {
        assertNull(system.uniqueOwner(), "Planets do not have owners, thus uniqueowner is null");
    }

    @Test
    public void uniqueOwnerTest_allPlanetsOwnedByAEON() {
        planetA.setCurrentOwner(Faction.AEON);
        planetB.setCurrentOwner(Faction.AEON);
        planetC.setCurrentOwner(Faction.AEON);
        assertEquals(Faction.AEON, system.uniqueOwner(), "All planets in SolarSystem owned by AEON");
    }
    @Test
    public void uniqueOwnerTest_notAllPlanetsOwnedByAeon() {
        planetC.setCurrentOwner(Faction.CYBRAN);
        assertNull(system.uniqueOwner(), "One planet not owned by AEON but by CYBRAN");
    }

    @Test
    public void isReachableTest_notReachable() {
        assertFalse(system.isReachable(Faction.AEON));
        assertFalse(system.isReachable(Faction.CYBRAN));
        assertFalse(system.isReachable(Faction.UEF));
        assertFalse(system.isReachable(Faction.SERAPHIM));
    }

    @Test
    public void isReachableTest_reachableThroughPlanet() {
        planetA.setCurrentOwner(Faction.AEON);
        assertTrue(system.isReachable(Faction.AEON), "A planet within the system is owned by AEON, AEON should reach the planet");
        assertFalse(system.isReachable(Faction.CYBRAN));
        assertFalse(system.isReachable(Faction.UEF));
        assertFalse(system.isReachable(Faction.SERAPHIM));
    }

    @Test
    public void isReachableTest_reachableThroughConnectedSystem() {
        planetConnectedSystem.setCurrentOwner(Faction.AEON);
        assertTrue(system.isReachable(Faction.AEON), "Connected system owned by AEON, AEON should reach the planet");
        assertFalse(system.isReachable(Faction.CYBRAN));
        assertFalse(system.isReachable(Faction.UEF));
        assertFalse(system.isReachable(Faction.SERAPHIM));
    }

    @Test
    public void isReachableTest_isolatedSystem() {
        system.setConnectedSystems(new HashSet<>());
        assertFalse(system.isReachable(Faction.AEON), "System is isolated, thus unreachable");
    }

}
