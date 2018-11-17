package com.faforever.gw.task.regular_income;

import com.faforever.gw.bpmn.task.regular_income.GiveRegularIncomeTask;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.service.CharacterService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GiveRegularIncomeTest {
    private GiveRegularIncomeTask task;
    @Mock
    private DelegateExecution delegateExecution;
    @Mock
    private CharacterRepository characterRepository;
    @Mock
    private CharacterService characterService;
    @Mock
    private GwCharacter character;

    @Before
    public void setUp() throws Exception {
        when(delegateExecution.getProcessInstance()).thenReturn(delegateExecution);
        when(delegateExecution.getBusinessKey()).thenReturn("test");

        when(delegateExecution.getVariable("character"))
                .thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));

//        task = new GiveRegularIncomeTask(characterService, characterRepository, gwServerProperties);
    }

    @Test
    public void dummy() throws Exception {
        when(characterRepository.findOne(UUID.fromString("11111111-1111-1111-1111-111111111111"))).thenReturn(character);
//        when(characterService.addIncome(character, GiveRegularIncomeTask);)

        task.execute(delegateExecution);
    }

}