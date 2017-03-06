package com.faforever.gw.bpmn.task.planetary_assault;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.bpmn.services.PlanetaryAssaultService;
import com.faforever.gw.model.*;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class ProcessGameResultTask implements JavaDelegate {
    private final PlanetaryAssaultService planetaryAssaultService;
    private final BattleRepository battleRepository;
    private final CharacterRepository characterRepository;

    @Inject
    public ProcessGameResultTask(PlanetaryAssaultService planetaryAssaultService, BattleRepository battleRepository, CharacterRepository characterRepository) {
        this.planetaryAssaultService = planetaryAssaultService;
        this.battleRepository = battleRepository;
        this.characterRepository = characterRepository;
    }

    @Override
    @Transactional(dontRollbackOn = BpmnError.class)
    public void execute(DelegateExecution execution) throws Exception {
        // Information: we don't change the actual battle here - see CloseAssaultTask instead
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution);
        log.debug("processGameResultTask for battle {}", accessor.getBusinessKey());

        GameResult gameResult = accessor.getGameResult();
        Battle battle = battleRepository.findOne(accessor.getBattleId());
        battle.setWinningFaction(gameResult.getWinner());
        accessor.setWinner(gameResult.getWinner() == accessor.getAttackingFaction() ? BattleRole.ATTACKER : BattleRole.DEFENDER);

        HashMap<UUID, GwCharacter> characterMap = new HashMap<>();
        HashMap<UUID, List<UUID>> killsMap = new HashMap<>();

        // first initialize the HashMaps
        for (GameCharacterResult characterResult : gameResult.getCharacterResults()) {
            UUID characterId = characterResult.getCharacter();
            GwCharacter character = characterRepository.findOne(characterId);
            characterMap.put(characterId, character);

            UUID killerId = characterResult.getKilledByCharacter();
            if (killerId != null) {
                killsMap.putIfAbsent(killerId, new ArrayList<>());
                killsMap.get(killerId).add(characterId);
            }
        }

        // now the HashMaps are filled with all values -> process results
        for (GameCharacterResult characterResult : gameResult.getCharacterResults()) {
            UUID characterId = characterResult.getCharacter();

            GwCharacter character = characterMap.get(characterId);
            BattleParticipant participant = battle.getParticipant(character).get();

            BattleParticipantResult participantResult = characterResult.getParticipantResult();
            participant.setResult(participantResult);
            log.debug("-> Character {} result: {}", characterId, participantResult.getName());

            if (characterResult.getParticipantResult() != BattleParticipantResult.DEATH) {
                // give XP for winning the battle
                if (gameResult.getWinner() == character.getFaction()) {
                    Long gainedXp = planetaryAssaultService.calcFactionVictoryXpForCharacter(battle, character);
                    character.setXp(character.getXp() + gainedXp);
                    log.info("Character {} gained {} xp in battle", character.getId(), gainedXp);
                }

                // give XP for killing characters
                if (killsMap.containsKey(characterId)) {
                    for (UUID killedId : killsMap.get(characterId)) {
                        GwCharacter killedCharacter = characterMap.get(killedId);
                        killedCharacter.setKiller(character);

                        if (character.getFaction() == killedCharacter.getFaction()) {
                            Long xpMalus = planetaryAssaultService.calcTeamkillXpMalus(character);
                            character.setXp(character.getXp() - xpMalus);
                            log.info("Teamkill detected: character {} killed {}, {} xp malus", characterId, killedId, xpMalus);
                        } else {
                            Long xpBonus = planetaryAssaultService.calcKillXpBonus(character, killedCharacter);
                            character.setXp(character.getXp() + xpBonus);
                            log.info("Character {} killed character {}, {} xp bonus", killedId, characterId, xpBonus);
                        }
                    }
                }
            }
        }
    }
}
