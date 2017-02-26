package com.faforever.gw.bpmn.task;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
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

    private final BattleRepository battleRepository;
    private final CharacterRepository characterRepository;

    @Inject
    public ProcessGameResultTask(BattleRepository battleRepository, CharacterRepository characterRepository) {
        this.battleRepository = battleRepository;
        this.characterRepository = characterRepository;
    }

    @Override
    @Transactional(dontRollbackOn = BpmnError.class)
    public void execute(DelegateExecution execution) throws Exception {
        log.debug("processGameResultTask for battle {}", execution.getProcessInstance().getBusinessKey());

        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution.getVariables());
        GameResult gameResult = accessor.getGameResult();

        Battle battle = battleRepository.getOne(gameResult.getBattle());
        Map<UUID, GameCharacterResult> results = gameResult.getCharacterResults();

        for(BattleParticipant battleParticipant : battle.getParticipants()){
            GwCharacter character = battleParticipant.getCharacter();
            GameCharacterResult result = results.get(character.getId());
            battleParticipant.setResult(result.getParticipantResult());

            switch(result.getParticipantResult()) {
                case DEATH:
                    gameResult.getCharacterKills().stream()
                            .filter(uuidPair -> uuidPair.getValue().equals(character.getId()))
                            .findFirst().ifPresent(uuidPair ->
                                battleParticipant.getCharacter().setKiller(characterRepository.getOne(uuidPair.getKey()))
                            );
                    break;
                case VICTORY:
                    log.info("Character {} gained xp for victory in battle {}", character.getId(), battle.getId());
                    // TODO: Implementation
                    // NO break required
                default:
                    gameResult.getCharacterKills().stream()
                            .filter(uuidPair -> uuidPair.getKey().equals(character.getId()))
                            .findFirst().ifPresent(uuidPair -> {
                                if(uuidPair.getKey().equals(uuidPair.getValue())) {
                                    log.info("Teamkill detected for character {}", character.getId());
                                } else {
                                    log.info("Character {} killed character {}", uuidPair.getKey(), uuidPair.getValue());
                                    // TODO: Implementation
                                }
                            }
                    );
            }
        }
    }
}
