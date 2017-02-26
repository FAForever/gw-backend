package com.faforever.gw.model.repository;

import com.faforever.gw.model.GwCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CharacterRepository extends JpaRepository<GwCharacter, UUID> {
    GwCharacter findByFafId(int fafId);
    @Query("select character from GwCharacter character where killer is null")
    List<GwCharacter> findActiveCharacters();
}
