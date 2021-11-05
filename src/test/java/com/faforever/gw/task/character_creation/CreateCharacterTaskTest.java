package com.faforever.gw.task.character_creation;

import com.faforever.gw.bpmn.services.CharacterCreationService;
import com.faforever.gw.bpmn.services.GwErrorService;
import com.faforever.gw.bpmn.task.character_creation.CreateCharacterTask;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Rank;
import com.faforever.gw.model.repository.RankRepository;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CreateCharacterTaskTest {
    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private GwErrorService gwErrorService;
    @Mock
    private CharacterCreationService characterCreationService;
    @Mock
    private RankRepository rankRepository;
    @Mock
    private Rank rankZero;

    @Captor
    private ArgumentCaptor<GwCharacter> argumentCaptor;

    private CreateCharacterTask task;

    @Before
    public void setUp() throws Exception {
        when(delegateExecution.getProcessInstance()).thenReturn(delegateExecution);
        when(delegateExecution.getBusinessKey()).thenReturn("test");

        task = new CreateCharacterTask(gwErrorService, characterCreationService, rankRepository);
    }

    @Test(expected = BpmnError.class)
    public void testSelectedNameInvalid() throws Exception {
        when(delegateExecution.getVariable("proposedNamesList")).thenReturn(List.of("A", "B"));
        when(delegateExecution.getVariable("selectedName")).thenReturn("C");
        when(gwErrorService.getBpmnErrorOf(any())).thenReturn(mock(BpmnError.class));

        task.execute(delegateExecution);
    }

    @Test
    public void testSuccess() throws Exception {
        when(delegateExecution.getVariable("proposedNamesList")).thenReturn(List.of("A", "B"));
        when(delegateExecution.getVariable("selectedName")).thenReturn("A");
        when(delegateExecution.getVariable("requestFafUser")).thenReturn(99L);
        when(delegateExecution.getVariable("requestedFaction")).thenReturn(Faction.UEF);
        when(delegateExecution.getVariable("selectedName")).thenReturn("A");
        when(rankRepository.getOne(1)).thenReturn(rankZero);

        task.execute(delegateExecution);

        verify(delegateExecution).setVariable(eq("newCharacterId"), any(String.class));
        verify(characterCreationService).persistNewCharacter(argumentCaptor.capture());

        GwCharacter character = argumentCaptor.getValue();

        assertThat(character.getFafId()).isEqualTo(99L);
        assertThat(character.getName()).isEqualTo("A");
        assertThat(character.getFaction()).isEqualTo(Faction.UEF);
        assertThat(character.getXp()).isEqualTo(0);
        assertThat(character.getRank()).isEqualTo(rankZero);
    }


}
