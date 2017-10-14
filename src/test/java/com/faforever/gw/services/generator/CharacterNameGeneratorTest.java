package com.faforever.gw.services.generator;

import com.faforever.gw.model.Faction;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.TestCase.assertTrue;

@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class CharacterNameGeneratorTest {
    private CharacterNameGenerator instance;

    @Before
    public void setUp() throws Exception {
        instance = new CharacterNameGenerator();
    }

    @Test
    public void testUEF() throws Exception {
        for (int i = 0; i < 1000; i++) {
            testFaction(Faction.UEF);
        }
    }

    @Test
    public void testCybran() throws Exception {
        for (int i = 0; i < 1000; i++) {
            testFaction(Faction.CYBRAN);
        }
    }

    @Test
    public void testAeon() throws Exception {
        for (int i = 0; i < 1000; i++) {
            testFaction(Faction.AEON);
        }
    }

    @Test
    public void testSeraphim() throws Exception {
        for (int i = 0; i < 1000; i++) {
            testFaction(Faction.SERAPHIM);
        }
    }

    private void testFaction(Faction faction) throws Exception {
        String[] names = instance.generateNames(faction);

        for (String name : names) {
            assertTrue(name.length() >= 5);
        }
    }
}
