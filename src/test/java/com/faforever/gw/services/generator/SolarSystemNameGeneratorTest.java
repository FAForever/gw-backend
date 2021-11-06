package com.faforever.gw.services.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(MockitoExtension.class)
public class SolarSystemNameGeneratorTest {
    private SolarSystemNameGenerator instance;

    @BeforeEach
    public void setUp() throws Exception {
        instance = new SolarSystemNameGenerator();
    }

    /**
     * Simple test to check for too short names.
     * Due to the nature of randomness this test is only of doubtful usage
     */
    @Test
    public void testNextMinimumLength() throws Exception {
        for (int i = 0; i < 100; i++) {
            String name = instance.next();
            assertTrue("Minimum length of 5 characters", name.length() >= 5);
        }
    }
}
