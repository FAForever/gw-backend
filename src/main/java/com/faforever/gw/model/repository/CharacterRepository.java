package com.faforever.gw.model.repository;

import com.faforever.gw.model.GwCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CharacterRepository extends JpaRepository<GwCharacter, UUID> {
    List<GwCharacter> findByFafId(long fafId);

    @Query("select character from GwCharacter character where faf_id = :id AND killer is null")
    Optional<GwCharacter> findActiveCharacterByFafId(@Param("id") long fafId);

    @Query("select character from GwCharacter character where killer is null")
    List<GwCharacter> findActiveCharacters();
}
