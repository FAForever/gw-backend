package com.faforever.gw.task.character_creation;

import com.faforever.gw.bpmn.task.character_creation.CheckUserCharactersTask;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.CharacterRepository;
import org.assertj.core.util.Lists;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
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

    @Before
    public void setUp() throws Exception {
        when(delegateExecution.getProcessInstance()).thenReturn(delegateExecution);
        when(delegateExecution.getBusinessKey()).thenReturn("test");

        when(delegateExecution.getVariable("requestFafUser")).thenReturn(99L);
        when(delegateExecution.getVariable("requestedFaction")).thenReturn(Faction.UEF);
        when(deadCharacter.getKiller()).thenReturn(mock(GwCharacter.class));

        task = new CheckUserCharactersTask(characterRepository);
    }

    @Test
    public void testNoExistingCharacters() throws Exception {
        when(characterRepository.findByFafId(99L)).thenReturn(Lists.emptyList());

        task.execute(delegateExecution);

        verify(delegateExecution).setVariable("hasActiveCharacter", false);
        verify(delegateExecution).setVariable("hasDeadCharacter", false);
        verify(delegateExecution).setVariable("factionMatches", false);
    }

    @Test
    public void testJustAnActiveCharacterWithFactionMismatch() throws Exception {
        when(characterRepository.findByFafId(99L)).thenReturn(Lists.newArrayList(activeCharacter));
        when(activeCharacter.getFaction()).thenReturn(Faction.AEON);

        task.execute(delegateExecution);

        verify(delegateExecution).setVariable("hasActiveCharacter", true);
        verify(delegateExecution).setVariable("hasDeadCharacter", false);
        verify(delegateExecution).setVariable("factionMatches", false);
    }

    @Test
    public void testJustAnActiveCharacterWithFactionMatch() throws Exception {
        when(characterRepository.findByFafId(99L)).thenReturn(Lists.newArrayList(activeCharacter));
        when(activeCharacter.getFaction()).thenReturn(Faction.UEF);

        task.execute(delegateExecution);

        verify(delegateExecution).setVariable("hasActiveCharacter", true);
        verify(delegateExecution).setVariable("hasDeadCharacter", false);
        verify(delegateExecution).setVariable("factionMatches", true);
    }

    @Test(expected = IllegalStateException.class)
    public void testActiveAndDeadCharacterWithDivergentFactions() throws Exception {
        when(characterRepository.findByFafId(99L)).thenReturn(Lists.newArrayList(activeCharacter, deadCharacter));
        when(activeCharacter.getFaction()).thenReturn(Faction.UEF);
        when(deadCharacter.getFaction()).thenReturn(Faction.AEON);

        task.execute(delegateExecution);
    }

    @Test
    public void testActiveAndDeadCharacterWithFactionMismatch() throws Exception {
        when(characterRepository.findByFafId(99L)).thenReturn(Lists.newArrayList(activeCharacter, deadCharacter));
        when(activeCharacter.getFaction()).thenReturn(Faction.AEON);
        when(deadCharacter.getFaction()).thenReturn(Faction.AEON);

        task.execute(delegateExecution);

        verify(delegateExecution).setVariable("hasActiveCharacter", true);
        verify(delegateExecution).setVariable("hasDeadCharacter", true);
        verify(delegateExecution).setVariable("factionMatches", false);
    }

    @Test
    public void testActiveAndDeadCharacterWithFactionMatch() throws Exception {
        when(characterRepository.findByFafId(99L)).thenReturn(Lists.newArrayList(activeCharacter, deadCharacter));
        when(activeCharacter.getFaction()).thenReturn(Faction.UEF);
        when(deadCharacter.getFaction()).thenReturn(Faction.UEF);

        task.execute(delegateExecution);

        verify(delegateExecution).setVariable("hasActiveCharacter", true);
        verify(delegateExecution).setVariable("hasDeadCharacter", true);
        verify(delegateExecution).setVariable("factionMatches", true);
    }
}
