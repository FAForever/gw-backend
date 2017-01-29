package com.faforever.gw.task;

import com.faforever.gw.bpmn.task.InitiateAssaultTask;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.ValidationHelper;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddCharacterToAssaultTest {
    @Mock
    DelegateExecution delegateExecution;
    @Mock
    GwCharacter gwCharacter;
    @Mock
    Planet planet;
    @Mock
    ValidationHelper validationHelper;

    InitiateAssaultTask initiateAssault;

    @Mock
    RuntimeService runtimeService;
    @Mock
    PlanetRepository planetRepository;
    @Mock
    CharacterRepository characterRepository;
    @Mock
    BattleRepository battleRepository;

    @Before
    public void setUp() throws Exception {
        initiateAssault = new InitiateAssaultTask(runtimeService, characterRepository, planetRepository, battleRepository, validationHelper);
    }

    @Test
    public void success() throws Exception {
        when(delegateExecution.getVariable("character"))
                .thenReturn(gwCharacter);


        initiateAssault.execute(delegateExecution);

        // TODO: make this test actually using JPA to check the validators passing through
    }

    // TODO: add test for each validator failing
}