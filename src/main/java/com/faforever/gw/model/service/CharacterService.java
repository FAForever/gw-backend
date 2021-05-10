package com.faforever.gw.model.service;

import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.outbound.HelloMessage;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CharacterService {
    private final CharacterRepository characterRepository;
    private final ClientMessagingService clientMessagingService;

    @Transactional
    public Optional<GwCharacter> getActiveCharacterByFafId(long fafId) {
        return characterRepository.findActiveCharacterByFafId(fafId);
    }

    public GwCharacter requireCharacter(UUID characterId) {
        return characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalStateException(String.format("Character does not exist: %s", characterId)));
    }

    public void onCharacterDeath(GwCharacter character) {
        log.info("Character {} died in battle", character.getId());
        clientMessagingService.sendToCharacter(new HelloMessage(null, null), character.getId());
    }
}
