package com.faforever.gw.services.generator;

import com.faforever.gw.model.Faction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class CharacterNameGeneratorTest {
    private CharacterNameGenerator instance;

    @BeforeEach
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
        List<String> names = instance.generateNames(faction);

        for (String name : names) {
            assertTrue(name.length() >= 5);
        }
    }
}
