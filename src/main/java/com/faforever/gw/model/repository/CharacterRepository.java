package com.faforever.gw.model.repository;

import com.faforever.gw.model.GwCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CharacterRepository extends JpaRepository<GwCharacter, UUID> {
    GwCharacter findByFafId(int fafId);
}
