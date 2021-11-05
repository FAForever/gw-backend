package com.faforever.gw.bpmn.task.character_creation;


import com.faforever.gw.bpmn.accessors.CharacterCreationAccessor;
import com.faforever.gw.bpmn.services.CharacterCreationService;
import com.faforever.gw.bpmn.services.GwErrorService;
import com.faforever.gw.bpmn.services.GwErrorType;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.RankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateCharacterTask implements JavaDelegate {
    private final GwErrorService gwErrorService;
    private final CharacterCreationService characterCreationService;
    private final RankRepository rankRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.debug("createCharacterTask");

        CharacterCreationAccessor accessor = CharacterCreationAccessor.of(execution);

        if (!accessor.getProposedNamesList().contains(accessor.getSelectedName())) {
            throw gwErrorService.getBpmnErrorOf(GwErrorType.NO_CREATION_WITH_INVALID_SELECTION);
        }

        GwCharacter character = new GwCharacter();
        character.setId(UUID.randomUUID());
        character.setFafId(accessor.getRequestFafUser());
        character.setName(accessor.getSelectedName());
        character.setFaction(accessor.getRequestedFaction());
        character.setXp(0L);
        character.setRank(rankRepository.getById(1));
        characterCreationService.persistNewCharacter(character);

        accessor.setNewCharacterId(character.getId());
    }
}
