package com.faforever.gw.task.regular_income;

import com.faforever.gw.bpmn.task.regular_income.GiveRegularIncomeTask;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.RankRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GiveRegularIncomeTest {
    @Mock
    private Battle battle;
    private GiveRegularIncomeTask task;
    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private CharacterRepository characterRepository;
    @Mock
    private GwCharacter character;
    @Mock
    private RankRepository rankRepository;

    @Before
    public void setUp() throws Exception {
        when(delegateExecution.getProcessInstance()).thenReturn(delegateExecution);
        when(delegateExecution.getBusinessKey()).thenReturn("test");

        when(delegateExecution.getVariable("character"))
                .thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));

        task = new GiveRegularIncomeTask();
    }

    // TODO: Add tests according to implementation

}