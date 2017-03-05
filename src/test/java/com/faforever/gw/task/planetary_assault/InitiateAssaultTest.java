package com.faforever.gw.task.planetary_assault;

import com.faforever.gw.bpmn.task.planetary_assault.InitiateAssaultTask;
import com.faforever.gw.model.*;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InitiateAssaultTest {
    @Mock
    private DelegateExecution delegateExecution;
    @Mock
    private ValidationHelper validationHelper;
    @Mock
    private PlanetRepository planetRepository;
    @Mock
    private CharacterRepository characterRepository;
    @Mock
    private BattleRepository battleRepository;
    @Mock
    private GwCharacter character;
    @Mock
    private Planet planet;

    private InitiateAssaultTask task;

    @Before
    public void setUp() throws Exception {
        when(delegateExecution.getProcessInstance()).thenReturn(delegateExecution);
        when(delegateExecution.getBusinessKey()).thenReturn("test");

        when(delegateExecution.getVariable("requestCharacter"))
                .thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        when(delegateExecution.getVariable("planet"))
                .thenReturn(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        when(delegateExecution.getVariable("battle"))
                .thenReturn(UUID.fromString("33333333-3333-3333-3333-333333333333"));

        when(characterRepository.findOne(any(UUID.class))).thenReturn(character);
        when(planetRepository.findOne(any(UUID.class))).thenReturn(planet);

        when(character.getFaction())
                .thenReturn(Faction.UEF);
        when(planet.getCurrentOwner())
                .thenReturn(Faction.CYBRAN);

        task = new InitiateAssaultTask(characterRepository, planetRepository, battleRepository, validationHelper);

    }

    @Test
    public void success() throws Exception {
        task.execute(delegateExecution);

        // Assert input taken from bpmn process
        verify(delegateExecution, atLeastOnce()).getVariable("requestCharacter");
        verify(delegateExecution, atLeastOnce()).getVariable("planet");
        verify(delegateExecution, atLeastOnce()).getVariable("battle");

        // Assert validation methods called correctly
        verify(validationHelper).validateCharacterFreeForGame(character);
        verify(validationHelper).validateAssaultOnPlanet(character, planet);

        // Assert attributes of battle
        ArgumentCaptor<Battle> battleArgument = ArgumentCaptor.forClass(Battle.class);
        verify(battleRepository).save(battleArgument.capture());

        Battle battle = battleArgument.getValue();
        assertNotNull(battle.getId());
        assertEquals(battle.getStatus(), BattleStatus.INITIATED);
        assertEquals(battle.getAttackingFaction(), Faction.UEF);
        assertEquals(battle.getDefendingFaction(), Faction.CYBRAN);
        assertEquals(battle.getPlanet(), planet);
        assertNull(battle.getWinningFaction());
        assertNotNull(battle.getInitiatedAt());
        assertNull(battle.getStartedAt());
        assertNull(battle.getEndedAt());

        List<BattleParticipant> battleParticipants = battle.getParticipants();
        assertEquals(battleParticipants.size(), 1);
        assertEquals(battleParticipants.stream().findFirst().get().getCharacter(), character);

        // Assert process variables
        verify(delegateExecution).setVariable(eq("battle"), any(UUID.class));
        verify(delegateExecution).setVariable("attackingFaction", Faction.UEF);
        verify(delegateExecution).setVariable("defendingFaction", Faction.CYBRAN);
    }

    @Test(expected = BpmnError.class)
    public void testValidateAssaultOnPlanetThrowsException() throws Exception {
        doThrow(new BpmnError("validationHelper.validateAssaultOnPlanet"))
                .when(validationHelper).validateAssaultOnPlanet(character, planet);

        task.execute(delegateExecution);
    }

    @Test(expected = BpmnError.class)
    public void testValidateCharacterFreeForGameThrowsException() throws Exception {
        doThrow(new BpmnError("validationHelper.validateCharacterFreeForGame"))
                .when(validationHelper).validateCharacterFreeForGame(character);

        task.execute(delegateExecution);
    }

}