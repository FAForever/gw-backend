package com.faforever.gw.services.generator;

import org.springframework.stereotype.Service;

@Service
public class SolarSystemNameGenerator {
    private long index = 1;

    public String next() {
        return "solar system #" + index++; // FIXME: Implement actual logic
    }
}
