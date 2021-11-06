package com.faforever.gw.task.character_creation;

import com.faforever.gw.bpmn.task.character_creation.CheckUserCharactersTask;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.CharacterRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckUserCharactersTaskTest {
    @Mock
    private DelegateExecution delegateExecution;
    @Mock
    private CharacterRepository characterRepository;
    @Mock
    private GwCharacter activeCharacter;
    @Mock
    private GwCharacter deadCharacter;

    private CheckUserCharactersTask task;

    @BeforeEach
    public void setUp() throws Exception {
        lenient().when(delegateExecution.getVariable("requestFafUser")).thenReturn(99L);
        lenient().when(delegateExecution.getVariable("requestedFaction")).thenReturn(Faction.UEF);
        lenient().when(deadCharacter.getKiller()).thenReturn(mock(GwCharacter.class));

        task = new CheckUserCharactersTask(characterRepository);
    }

    @Test
    public void testNoExistingCharacters() throws Exception {
        when(characterRepository.findByFafId(99L)).thenReturn(List.of());

        task.execute(delegateExecution);

        verify(delegateExecution).setVariable("hasActiveCharacter", false);
        verify(delegateExecution).setVariable("hasDeadCharacter", false);
        verify(delegateExecution).setVariable("factionMatches", false);
    }

    @Test
    public void testJustAnActiveCharacterWithFactionMismatch() throws Exception {
        when(characterRepository.findByFafId(99L)).thenReturn(List.of(activeCharacter));
        when(activeCharacter.getFaction()).thenReturn(Faction.AEON);

        task.execute(delegateExecution);

        verify(delegateExecution).setVariable("hasActiveCharacter", true);
        verify(delegateExecution).setVariable("hasDeadCharacter", false);
        verify(delegateExecution).setVariable("factionMatches", false);
    }

    @Test
    public void testJustAnActiveCharacterWithFactionMatch() throws Exception {
        when(characterRepository.findByFafId(99L)).thenReturn(List.of(activeCharacter));
        when(activeCharacter.getFaction()).thenReturn(Faction.UEF);

        task.execute(delegateExecution);

        verify(delegateExecution).setVariable("hasActiveCharacter", true);
        verify(delegateExecution).setVariable("hasDeadCharacter", false);
        verify(delegateExecution).setVariable("factionMatches", true);
    }

    @Test
    public void testActiveAndDeadCharacterWithDivergentFactions() throws Exception {
        when(characterRepository.findByFafId(99L)).thenReturn(List.of(activeCharacter, deadCharacter));
        when(activeCharacter.getFaction()).thenReturn(Faction.UEF);
        when(deadCharacter.getFaction()).thenReturn(Faction.AEON);

        assertThrows(IllegalStateException.class, () -> task.execute(delegateExecution));
    }

    @Test
    public void testActiveAndDeadCharacterWithFactionMismatch() throws Exception {
        when(characterRepository.findByFafId(99L)).thenReturn(List.of(activeCharacter, deadCharacter));
        when(activeCharacter.getFaction()).thenReturn(Faction.AEON);
        when(deadCharacter.getFaction()).thenReturn(Faction.AEON);

        task.execute(delegateExecution);

        verify(delegateExecution).setVariable("hasActiveCharacter", true);
        verify(delegateExecution).setVariable("hasDeadCharacter", true);
        verify(delegateExecution).setVariable("factionMatches", false);
    }

    @Test
    public void testActiveAndDeadCharacterWithFactionMatch() throws Exception {
        when(characterRepository.findByFafId(99L)).thenReturn(List.of(activeCharacter, deadCharacter));
        when(activeCharacter.getFaction()).thenReturn(Faction.UEF);
        when(deadCharacter.getFaction()).thenReturn(Faction.UEF);

        task.execute(delegateExecution);

        verify(delegateExecution).setVariable("hasActiveCharacter", true);
        verify(delegateExecution).setVariable("hasDeadCharacter", true);
        verify(delegateExecution).setVariable("factionMatches", true);
    }
}
