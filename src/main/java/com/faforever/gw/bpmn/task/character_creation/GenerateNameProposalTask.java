package com.faforever.gw.bpmn.task.character_creation;


import com.faforever.gw.bpmn.accessors.CharacterCreationAccessor;
import com.faforever.gw.services.generator.CharacterNameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenerateNameProposalTask implements JavaDelegate {
    private final CharacterNameGenerator characterNameGenerator;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.debug("generateNameProposalTask");

        CharacterCreationAccessor accessor = CharacterCreationAccessor.of(execution);

        List<String> proposedNames = characterNameGenerator.generateNames(accessor.getRequestedFaction());
        accessor.setProposedNamesList(proposedNames);
        log.debug("-> proposedNames: {}", proposedNames);
    }
}
