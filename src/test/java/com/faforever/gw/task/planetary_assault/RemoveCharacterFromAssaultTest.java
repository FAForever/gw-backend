package com.faforever.gw.task.planetary_assault;

import com.faforever.gw.bpmn.task.planetary_assault.RemoveCharacterFromAssaultTask;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemoveCharacterFromAssaultTest {
    @Mock
    private Battle battle;
    private RemoveCharacterFromAssaultTask task;
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
        when(character.getId()).thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        when(battleRepository.findOne(any(UUID.class))).thenReturn(battle);
        when(planetRepository.findOne(any(UUID.class))).thenReturn(planet);

        when(battle.getAttackingFaction()).thenReturn(Faction.UEF);
        when(battle.getDefendingFaction()).thenReturn(Faction.CYBRAN);
        ArrayList<BattleParticipant> battleParticipants = new ArrayList<>();
        when(battle.getParticipants()).thenReturn(battleParticipants);


        task = new RemoveCharacterFromAssaultTask(characterRepository, battleRepository, validationHelper);


        // default setUp
        when(character.getFaction()).thenReturn(Faction.UEF);
        when(delegateExecution.getVariable("attackerCount")).thenReturn(1);
    }

    @Test
    public void testAttackerLeftSuccess() throws Exception {
        // Setup attacker
        BattleParticipant attackerParticipant = mock(BattleParticipant.class);
        when(attackerParticipant.getCharacter()).thenReturn(character);
        battle.getParticipants().add(attackerParticipant);

        // Setup defender
        GwCharacter defenderCharacter = mock(GwCharacter.class);
        when(defenderCharacter.getFaction()).thenReturn(Faction.CYBRAN);
        when(defenderCharacter.getId()).thenReturn(UUID.fromString("12345678-1111-1111-1111-111111111111"));
        BattleParticipant defenderParticipant = mock(BattleParticipant.class);
        when(defenderParticipant.getCharacter()).thenReturn(defenderCharacter);
        battle.getParticipants().add(defenderParticipant);

        task.execute(delegateExecution);

        // Assert input taken from bpmn process
        verify(delegateExecution).getVariable("battle");
        verify(delegateExecution).getVariable("requestCharacter");
        verify(delegateExecution).getVariable("attackerCount");

        // Assert validation methods called correctly
        verify(validationHelper).validateCharacterInBattle(character, battle, true);

        // Assert attributes of battle
        ArgumentCaptor<Battle> battleArgument = ArgumentCaptor.forClass(Battle.class);
        verify(battleRepository).save(battleArgument.capture());

        Battle battle = battleArgument.getValue();
        List<BattleParticipant> battleParticipants = battle.getParticipants();
        assertEquals(battleParticipants.size(), 1);
        assertEquals(battleParticipants.stream().findFirst().get().getCharacter(), defenderCharacter);

        // Assert process variables
        verify(delegateExecution).setVariable("attackerCount", 0);
        verify(delegateExecution).setVariable("winner", "defender");
    }

    @Test
    public void testDefenderLeftSuccess() throws Exception {
        when(character.getFaction()).thenReturn(Faction.CYBRAN);
        when(delegateExecution.getVariable("defenderCount")).thenReturn(1);

        // Setup defender
        BattleParticipant defenderParticipant = mock(BattleParticipant.class);
        when(defenderParticipant.getCharacter()).thenReturn(character);
        battle.getParticipants().add(defenderParticipant);

        // Setup defender
        GwCharacter attackerCharacter = mock(GwCharacter.class);
        when(attackerCharacter.getFaction()).thenReturn(Faction.UEF);
        when(attackerCharacter.getId()).thenReturn(UUID.fromString("12345678-1111-1111-1111-111111111111"));
        BattleParticipant attackerParticipant = mock(BattleParticipant.class);
        when(attackerParticipant.getCharacter()).thenReturn(attackerCharacter);
        battle.getParticipants().add(attackerParticipant);

        task.execute(delegateExecution);

        // Assert input taken from bpmn process
        verify(delegateExecution).getVariable("battle");
        verify(delegateExecution).getVariable("requestCharacter");
        verify(delegateExecution).getVariable("defenderCount");

        // Assert validation methods called correctly
        verify(validationHelper).validateCharacterInBattle(character, battle, true);

        // Assert attributes of battle
        ArgumentCaptor<Battle> battleArgument = ArgumentCaptor.forClass(Battle.class);
        verify(battleRepository).save(battleArgument.capture());

        Battle battle = battleArgument.getValue();
        List<BattleParticipant> battleParticipants = battle.getParticipants();
        assertEquals(battleParticipants.size(), 1);
        assertEquals(battleParticipants.stream().findFirst().get().getCharacter(), attackerCharacter);

        // Assert process variables
        verify(delegateExecution).setVariable("defenderCount", 0);
        verify(delegateExecution, never()).setVariable(eq("winner"), anyString());
    }

    @Test(expected = BpmnError.class)
    public void testValidateCharacterInBattleThrowsException() throws Exception {
        doThrow(new BpmnError("validationHelper.validateCharacterInBattle"))
                .when(validationHelper).validateCharacterInBattle(character, battle, true);

        task.execute(delegateExecution);
    }

}