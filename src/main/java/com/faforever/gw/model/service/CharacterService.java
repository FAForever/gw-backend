package com.faforever.gw.model.service;

import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.outbound.HelloMessage;
import com.faforever.gw.model.CreditJournalEntry;
import com.faforever.gw.model.CreditJournalEntryReason;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.ReinforcementsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class CharacterService {
    private final CharacterRepository characterRepository;
    private final ClientMessagingService clientMessagingService;
    private final ReinforcementsRepository reinforcementsRepository;

    @Inject
    public CharacterService(CharacterRepository characterRepository, ClientMessagingService clientMessagingService, ReinforcementsRepository reinforcementsRepository) {
        this.characterRepository = characterRepository;
        this.clientMessagingService = clientMessagingService;
        this.reinforcementsRepository = reinforcementsRepository;
    }

    @Transactional
    public GwCharacter getActiveCharacterByFafId(long fafId) {
        return characterRepository.findActiveCharacterByFafId(fafId);
    }

    public GwCharacter requireCharacter(UUID characterId) {
        return Optional.ofNullable(characterRepository.getOne(characterId))
                .orElseThrow(() -> new IllegalStateException(String.format("Character does not exist: %s", characterId)));
    }

    public void onCharacterDeath(GwCharacter character) {
        log.info("Character {} died in battle", character.getId());
        clientMessagingService.sendToCharacter(new HelloMessage(null, null), character.getId());
    }

    @Transactional
    public double getAvailableCredits(GwCharacter character) {
        return character.getCreditJournalList().stream()
                .mapToDouble(CreditJournalEntry::getAmount).sum();
    }

    @Transactional
    public void addIncome(GwCharacter character, double amount) {
        character.getCreditJournalList().add(new CreditJournalEntry(
                character,
                null,
                CreditJournalEntryReason.REGULAR_INCOME,
                amount
        ));
        characterRepository.save(character);
    }
}
