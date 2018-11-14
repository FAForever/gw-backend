package com.faforever.gw.model.service;

import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.outbound.HelloMessage;
import com.faforever.gw.model.CreditJournalEntry;
import com.faforever.gw.model.CreditJournalEntryReason;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.ReinforcementsGroup;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.ReinforcementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class CharacterService {
    private final CharacterRepository characterRepository;
    private final ClientMessagingService clientMessagingService;
    private final ReinforcementRepository reinforcementRepository;

    @Inject
    public CharacterService(CharacterRepository characterRepository, ClientMessagingService clientMessagingService, ReinforcementRepository reinforcementRepository) {
        this.characterRepository = characterRepository;
        this.clientMessagingService = clientMessagingService;
        this.reinforcementRepository = reinforcementRepository;
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
    public Map<ReinforcementsGroup, Integer> getAvailableReinforcements(GwCharacter character) {
        Map<ReinforcementsGroup, Integer> res = new HashMap<>();
        reinforcementRepository.findAll().forEach(r -> res.put(r, 0));
        character.getCreditJournalList().stream()
                .filter(entry -> entry.getReason() == CreditJournalEntryReason.REINFORCEMENTS)
                .map(CreditJournalEntry::getReinforcementsTransaction)
                .forEach(transaction -> res.put(transaction.getGroup(), res.get(transaction.getGroup()) + transaction.getQuantity()));

        return res;
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
