package com.faforever.gw.task.generic;

import com.faforever.gw.bpmn.task.generic.SelectAllActiveCharactersTask;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.CharacterRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SelectAllActiveCharactersTest {

    @Mock
    private DelegateExecution delegateExecution;
    @Mock
    private CharacterRepository characterRepository;

    @Captor
    private ArgumentCaptor<List<UUID>> captor;

    private SelectAllActiveCharactersTask task;


    @Before
    public void setUp() throws Exception {
        task = new SelectAllActiveCharactersTask(characterRepository);
    }

    @Test
    public void testNoActiveCharacters() throws Exception {
        when(characterRepository.findActiveCharacters()).thenReturn(Collections.emptyList());

        task.execute(delegateExecution);

        verify(delegateExecution).setVariable("activeCharacters", Collections.emptyList());
    }

    @Test
    public void testSomeActiveCharacters() throws Exception {
        GwCharacter dummy1 = mock(GwCharacter.class);
        UUID uuid1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(dummy1.getId()).thenReturn(uuid1);

        GwCharacter dummy2 = mock(GwCharacter.class);
        UUID uuid2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
        when(dummy2.getId()).thenReturn(uuid2);

        List<GwCharacter> characterList = Arrays.asList(dummy1, dummy2);
        when(characterRepository.findActiveCharacters()).thenReturn(characterList);

        task.execute(delegateExecution);

        verify(delegateExecution).setVariable(eq("activeCharacters"), captor.capture());
        List<UUID> characters = captor.getValue();

        assertEquals(characters.size(), 2);
        assertEquals(characters.get(0), uuid1);
        assertEquals(characters.get(1), uuid2);
    }
}
