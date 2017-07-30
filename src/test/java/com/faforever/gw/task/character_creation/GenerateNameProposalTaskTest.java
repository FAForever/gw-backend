package com.faforever.gw.task.character_creation;

import com.faforever.gw.bpmn.task.character_creation.GenerateNameProposalTask;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GenerateNameProposalTaskTest {
    @Captor
    ArgumentCaptor<String> variableCaptor;
    @Captor
    ArgumentCaptor<List<String>> listCaptor;
    @Mock
    private DelegateExecution delegateExecution;
    private GenerateNameProposalTask task;

    @Before
    public void setUp() throws Exception {
        when(delegateExecution.getProcessInstance()).thenReturn(delegateExecution);
        when(delegateExecution.getBusinessKey()).thenReturn("test");

        task = new GenerateNameProposalTask();
    }

    @Test
    public void test() throws Exception {
        task.execute(delegateExecution);

        verify(delegateExecution).setVariable(variableCaptor.capture(), listCaptor.capture());
        assertThat(variableCaptor.getValue()).isEqualTo("proposedNamesList");
        assertThat(listCaptor.getValue().size()).isEqualTo(5);
    }


}
