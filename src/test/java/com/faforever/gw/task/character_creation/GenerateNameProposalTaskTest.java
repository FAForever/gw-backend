package com.faforever.gw.task.character_creation;

import com.faforever.gw.bpmn.task.character_creation.GenerateNameProposalTask;
import com.faforever.gw.services.generator.CharacterNameGenerator;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class GenerateNameProposalTaskTest {
    @Captor
    ArgumentCaptor<String> variableCaptor;
    @Captor
    ArgumentCaptor<List<String>> listCaptor;
    @Mock
    private DelegateExecution delegateExecution;
    @Mock
    private CharacterNameGenerator characterNameGenerator;

    private GenerateNameProposalTask task;

    @Before
    public void setUp() throws Exception {
        when(delegateExecution.getProcessInstance()).thenReturn(delegateExecution);
        when(characterNameGenerator.generateNames(any())).thenReturn(List.of("1", "2", "3", "4", "5"));
        when(delegateExecution.getBusinessKey()).thenReturn("test");

        task = new GenerateNameProposalTask(characterNameGenerator);
    }

    @Test
    public void test() throws Exception {
        task.execute(delegateExecution);

        verify(delegateExecution).setVariable(variableCaptor.capture(), listCaptor.capture());
        assertThat(variableCaptor.getValue()).isEqualTo("proposedNamesList");
        assertThat(listCaptor.getValue().size()).isEqualTo(5);
    }


}
