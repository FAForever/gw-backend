package com.faforever.gw.model.repository;

import com.faforever.gw.model.Battle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BattleRepository extends JpaRepository<Battle, UUID> {

}
