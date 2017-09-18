package com.faforever.gw.services.generator;

import com.faforever.gw.model.Ground;
import com.faforever.gw.model.Map;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.repository.MapRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class MapSelectorService {
    private static Map dummyMap;

    private final MapRepository mapRepository;

    @Inject
    public MapSelectorService(MapRepository mapRepository) {
        this.mapRepository = mapRepository;

        // FIXME: This should not be required, map list should be loaded into db by hand
        this.dummyMap = new Map();
        dummyMap.setGround(Ground.SOIL);
        dummyMap.setFafMapId(1);
        dummyMap.setFafMapVersion(1);
        dummyMap.setSize(10);
        dummyMap.setTotalSlots(2);
        mapRepository.save(dummyMap);
    }

    public Map chooseMapFor(Planet planet) {
        return dummyMap; // FIXME: Implement actual logic here
    }
}
