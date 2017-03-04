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
import java.util.Map;
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

        BattleRole winner = gameResult.getWinner() == accessor.getAttackingFaction() ? BattleRole.ATTACKER : BattleRole.DEFENDER;
        accessor.setWinner(winner);

        Battle battle = battleRepository.getOne(gameResult.getBattle());
        Map<UUID, GameCharacterResult> results = gameResult.getCharacterResults();

        for (BattleParticipant battleParticipant : battle.getParticipants()) {
            GwCharacter character = battleParticipant.getCharacter();
            GameCharacterResult result = results.get(character.getId());
            battleParticipant.setResult(result.getParticipantResult());

            // if killed, store the killer
            if (result.getParticipantResult() == BattleParticipantResult.DEATH) {
                gameResult.getCharacterKills().stream()
                        .filter(uuidPair -> uuidPair.getValue().equals(character.getId()))
                        .findFirst().ifPresent(uuidPair ->
                        battleParticipant.getCharacter().setKiller(characterRepository.getOne(uuidPair.getKey()))
                );
            } else {
                // if participants faction won, gain XP
                if (battle.getWinningFaction() == character.getFaction()) {
                    Long gainedXp = planetaryAssaultService.calcFactionVictoryXpForCharacter(battle, character);
                    character.setXp(character.getXp() + gainedXp);
                    log.info("Character {} gained {} xp in battle", character.getId(), gainedXp);
                }

                // process all kills for this character
                gameResult.getCharacterKills().stream()
                        .filter(uuidPair -> uuidPair.getKey().equals(character.getId()))
                        .forEach(uuidPair -> {
                                    GwCharacter victim = characterRepository.getOne(uuidPair.getValue());

                                    if (character.getFaction() == victim.getFaction()) {
                                        Long xpMalus = planetaryAssaultService.calcTeamkillXpMalus(character);
                                        character.setXp(character.getXp() - xpMalus);
                                        log.info("Teamkill detected: character {} killed {}, {} xp malus", character.getId(), uuidPair.getValue(), xpMalus);
                                    } else {
                                        Long xpBonus = planetaryAssaultService.calcKillXpBonus(character, victim);
                                        character.setXp(character.getXp() + xpBonus);
                                        log.info("Character {} killed character {}, {} xp bonus", uuidPair.getKey(), uuidPair.getValue(), xpBonus);
                                    }
                                }
                        );
            }
        }
    }
}
