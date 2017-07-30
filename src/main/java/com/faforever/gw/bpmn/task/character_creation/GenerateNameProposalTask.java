package com.faforever.gw.bpmn.task.character_creation;


import com.faforever.gw.bpmn.accessors.CharacterCreationAccessor;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@Component
public class GenerateNameProposalTask implements JavaDelegate {

    @Inject
    public GenerateNameProposalTask() {
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.debug("generateNameProposalTask");

        CharacterCreationAccessor accessor = CharacterCreationAccessor.of(execution);

        // TODO: Actually generate names
        List<String> proposedNames = ImmutableList.of("Name1", "Name2", "Name3", "Name4", "Name5");
        accessor.setProposedNamesList(proposedNames);
    }
}
