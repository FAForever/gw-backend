package com.faforever.gw.bpmn.task.generic;


import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SelectAllActiveCharactersTask implements JavaDelegate {
    private final CharacterRepository characterRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.debug("selectAllActiveCharactersTask");

        List<GwCharacter> activeCharacters = characterRepository.findActiveCharacters();

        List<UUID> activeCharacterIds = new ArrayList<>();
        activeCharacters.stream().forEach(gwCharacter -> activeCharacterIds.add(gwCharacter.getId()));

        execution.setVariable("activeCharacters", activeCharacterIds);
        log.debug("-> set activeCharacters = {}", activeCharacterIds);
    }
}
