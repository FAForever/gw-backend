package com.faforever.gw.model.repository;

import com.faforever.gw.model.Map;
import com.faforever.gw.model.Planet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MapRepository extends JpaRepository<Map, UUID> {
}
