package com.faforever.gw.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;


public class SolarSystemTest {

    SolarSystem system, connectedSystem;
    Planet planetA, planetB, planetC, planetConnectedSystem;

    @Before
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
        Assert.assertEquals("Planets do not have owners, thus uniqueowner is null", null, system.uniqueOwner());
    }

    @Test
    public void uniqueOwnerTest_allPlanetsOwnedByAEON() {
        planetA.setCurrentOwner(Faction.AEON);
        planetB.setCurrentOwner(Faction.AEON);
        planetC.setCurrentOwner(Faction.AEON);
        Assert.assertEquals("All planets in SolarSystem owned by AEON", Faction.AEON, system.uniqueOwner());
    }
    @Test
    public void uniqueOwnerTest_notAllPlanetsOwnedByAeon() {
        planetC.setCurrentOwner(Faction.CYBRAN);
        Assert.assertEquals("One planet not owned by AEON but by CYBRAN", null, system.uniqueOwner());
    }

    @Test
    public void isReachableTest_notReachable() {
        Assert.assertFalse(system.isReachable(Faction.AEON));
        Assert.assertFalse(system.isReachable(Faction.CYBRAN));
        Assert.assertFalse(system.isReachable(Faction.UEF));
        Assert.assertFalse(system.isReachable(Faction.SERAPHIM));
    }

    @Test
    public void isReachableTest_reachableByAEON() {
        planetConnectedSystem.setCurrentOwner(Faction.AEON);
        Assert.assertTrue("Connected system owned by AEON, aoen should reach the planet", system.isReachable(Faction.AEON));
        Assert.assertFalse(system.isReachable(Faction.CYBRAN));
        Assert.assertFalse(system.isReachable(Faction.UEF));
        Assert.assertFalse(system.isReachable(Faction.SERAPHIM));
    }

    @Test
    public void isReachableTest_isolatedSystem() {
        system.setConnectedSystems(new HashSet<>());
        Assert.assertFalse("System is isolated, thus unreachable", system.isReachable(Faction.AEON));
    }

}
