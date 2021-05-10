package com.faforever.gw.model.service;

import com.faforever.gw.bpmn.services.PlanetaryAssaultService;
import com.faforever.gw.model.*;
import com.faforever.gw.model.repository.BattleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class BattleService {
    private final BattleRepository battleRepository;
    private final PlanetaryAssaultService planetaryAssaultService;
    private final CharacterService characterService;

    public void processGameResult(GameResult result) {
        Battle battle = Optional.ofNullable(battleRepository.getOne(result.getBattle()))
                .orElseThrow(() -> new IllegalStateException(String.format("Battle does not exist: %s", result.getBattle())));

        battle.setWinningFaction(result.getWinner());

        // Map<UUID, List<UUID>> characterKills

        Map<UUID, GwCharacter> characterMap = new HashMap<>();
        battle.getParticipants().stream()
                .map(BattleParticipant::getCharacter)
                .forEach(character -> characterMap.put(character.getId(), character));

        Map<GwCharacter, List<GwCharacter>> killsMap = new HashMap<>();
        result.getCharacterResults()
                .forEach(characterResult -> {
                    GwCharacter character = characterMap.get(characterResult.getCharacter());

                    UUID killerUUID = characterResult.getKilledByCharacter();
                    GwCharacter killer = characterMap.get(killerUUID);

                    if (killerUUID != null) {
                        killsMap.putIfAbsent(killer, new ArrayList<>());
                        killsMap.get(killer).add(character);
                    }
                });

        for (GameCharacterResult characterResult : result.getCharacterResults()) {
            GwCharacter character = characterMap.get(characterResult.getCharacter());

            BattleParticipant participant = battle.getParticipant(character)
                    .orElseThrow(() -> new IllegalStateException(String.format("Character %s did not participate in battle %s", character.getId(), battle.getId())));

            BattleParticipantResult participantResult = characterResult.getParticipantResult();
            participant.setResult(participantResult);
            log.debug("-> Character {} result: {}", character.getId(), participantResult.getName());

            if (characterResult.getParticipantResult() != BattleParticipantResult.DEATH) {
                // give XP for winning the battle
                if (result.getWinner() == character.getFaction()) {
                    Long gainedXp = planetaryAssaultService.calcFactionVictoryXpForCharacter(battle, character);
                    character.setXp(character.getXp() + gainedXp);
                    log.info("Character {} gained {} xp in battle", character.getId(), gainedXp);
                }

                // give XP for killing characters
                if (killsMap.containsKey(character)) {
                    for (GwCharacter killed : killsMap.get(character)) {
                        GwCharacter killedCharacter = characterMap.get(killed.getId());
                        killedCharacter.setKiller(character);

                        if (character.getFaction() == killedCharacter.getFaction()) {
                            Long xpMalus = planetaryAssaultService.calcTeamkillXpMalus(character);
                            character.setXp(character.getXp() - xpMalus);
                            log.info("Teamkill detected: character {} killed {}, {} xp malus", character.getId(), killed, xpMalus);
                        } else {
                            Long xpBonus = planetaryAssaultService.calcKillXpBonus(character, killedCharacter);
                            character.setXp(character.getXp() + xpBonus);
                            log.info("Character {} killed character {}, {} xp bonus", killed, character.getId(), xpBonus);
                        }
                    }
                }
            } else {
                characterService.onCharacterDeath(character);
            }
        }

    }

    private GwCharacter getCharacter(UUID characterId) {
        return characterService.requireCharacter(characterId);
    }
}
