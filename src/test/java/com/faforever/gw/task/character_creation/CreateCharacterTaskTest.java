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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
    public void setUp() throws Exception {
        task = new CreateCharacterTask(gwErrorService, characterCreationService, rankRepository);
    }

    @Test
    public void testSelectedNameInvalid() throws Exception {
        when(delegateExecution.getVariable("proposedNamesList")).thenReturn(List.of("A", "B"));
        when(delegateExecution.getVariable("selectedName")).thenReturn("C");
        when(gwErrorService.getBpmnErrorOf(any())).thenReturn(mock(BpmnError.class));

        assertThrows(BpmnError.class, () -> task.execute(delegateExecution));
    }

    @Test
    public void testSuccess() throws Exception {
        when(delegateExecution.getVariable("proposedNamesList")).thenReturn(List.of("A", "B"));
        when(delegateExecution.getVariable("selectedName")).thenReturn("A");
        when(delegateExecution.getVariable("requestFafUser")).thenReturn(99L);
        when(delegateExecution.getVariable("requestedFaction")).thenReturn(Faction.UEF);
        when(delegateExecution.getVariable("selectedName")).thenReturn("A");
        when(rankRepository.getById(1)).thenReturn(rankZero);

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
