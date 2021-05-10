package com.faforever.gw.bpmn.task.character_creation;


import com.faforever.gw.bpmn.accessors.CharacterCreationAccessor;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckUserCharactersTask implements JavaDelegate {
    private final CharacterRepository characterRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.debug("checkUserCharactersTask");

        CharacterCreationAccessor accessor = CharacterCreationAccessor.of(execution);

        boolean hasActiveCharacter = false;
        boolean hasDeadCharacter = false;
        Faction previousFaction = null;
        boolean factionMatches = false;

        List<GwCharacter> characterList = characterRepository.findByFafId(accessor.getRequestFafUser());

        for (GwCharacter character : characterList) {
            if (previousFaction != null && previousFaction != character.getFaction()) {
                throw new IllegalStateException(MessageFormat.format("User {0} has dead characters of different factions", accessor.getRequestFafUser()));
            }

            if (character.getKiller() == null) {
                hasActiveCharacter = true;
            } else {
                hasDeadCharacter = true;
            }

            previousFaction = character.getFaction();
        }

        factionMatches = previousFaction == accessor.getRequestedFaction();

        log.info("User {} has following state: hasActiveCharacter={0}, hasDeadCharacter={1}, previousFaction={2}", hasActiveCharacter, hasDeadCharacter, previousFaction);
        accessor.setHasActiveCharacter(hasActiveCharacter)
                .setHasDeadCharacter(hasDeadCharacter)
                .setFactionMatches(factionMatches);
    }
}
