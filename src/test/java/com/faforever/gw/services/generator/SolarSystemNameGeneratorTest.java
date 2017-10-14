package com.faforever.gw.services.generator;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.springframework.test.util.AssertionErrors.assertTrue;

@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class SolarSystemNameGeneratorTest {
    private SolarSystemNameGenerator instance;

    @Before
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
