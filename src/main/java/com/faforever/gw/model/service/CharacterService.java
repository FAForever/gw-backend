package com.faforever.gw.model.service;

import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.CharacterRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Service
public class CharacterService {
    private final CharacterRepository characterRepository;

    @Inject
    public CharacterService(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    @Transactional
    public GwCharacter getActiveCharacterByFafId(long fafId) {
        return characterRepository.findActiveCharacterByFafId(fafId);
    }
}
