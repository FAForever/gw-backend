package com.faforever.gw.task.generic;

import com.faforever.gw.bpmn.task.generic.SelectAllActiveCharactersTask;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.CharacterRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SelectAllActiveCharactersTest {

    @Mock
    private DelegateExecution delegateExecution;
    @Mock
    private CharacterRepository characterRepository;

    private SelectAllActiveCharactersTask task;


    @Before
    public void setUp() throws Exception {
        task = new SelectAllActiveCharactersTask(characterRepository);
    }

    @Test
    public void testNoActiveCharacters() throws Exception {
        when(characterRepository.findActiveCharacters()).thenReturn(Collections.EMPTY_LIST);

        task.execute(delegateExecution);

        verify(delegateExecution).setVariable("activeCharacters", Collections.EMPTY_LIST);
    }

    @Test
    public void testSomeActiveCharacters() throws Exception {
        List<GwCharacter> characterList = Arrays.asList(mock(GwCharacter.class), mock(GwCharacter.class));
        when(characterRepository.findActiveCharacters()).thenReturn(characterList);

        task.execute(delegateExecution);

        verify(delegateExecution).setVariable(eq("activeCharacters"), anyListOf(UUID.class));
    }
}
