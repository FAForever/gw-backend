package com.faforever.gw.task.planetary_assault;

import com.faforever.gw.bpmn.task.planetary_assault.AddCharacterToAssaultTask;
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
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AddCharacterToAssaultTest {
    @Mock
    private Battle battle;
    private AddCharacterToAssaultTask task;
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
    @Mock
    private Map map;

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

        when(characterRepository.getOne(any(UUID.class))).thenReturn(character);
        when(character.getId()).thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        when(battleRepository.getOne(any(UUID.class))).thenReturn(battle);
        when(planetRepository.getOne(any(UUID.class))).thenReturn(planet);

        when(battle.getAttackingFaction()).thenReturn(Faction.UEF);
        when(battle.getDefendingFaction()).thenReturn(Faction.CYBRAN);
        when(battle.getParticipants()).thenReturn(new ArrayList<>());
        when(planet.getMap()).thenReturn(map);
        when(map.getTotalSlots()).thenReturn(4);

        task = new AddCharacterToAssaultTask(characterRepository, battleRepository, planetRepository, validationHelper);


        // default setUp
        when(character.getFaction()).thenReturn(Faction.UEF);
        when(delegateExecution.getVariable("attackerCount")).thenReturn(0);
    }

    @Test
    public void testAttackerJoinedSuccess() throws Exception {
        task.execute(delegateExecution);

        // Assert input taken from bpmn process
        verify(delegateExecution).getVariable("battle");
        verify(delegateExecution).getVariable("planet");
        verify(delegateExecution).getVariable("requestCharacter");
        verify(delegateExecution).getVariable("attackerCount");

        // Assert validation methods called correctly
        verify(validationHelper).validateCharacterInBattle(character, battle, false);
        verify(validationHelper).validateCharacterFreeForGame(character);
        verify(validationHelper).validateOpenSlotForCharacter(battle, BattleRole.ATTACKER);

        // Assert attributes of battle
        ArgumentCaptor<Battle> battleArgument = ArgumentCaptor.forClass(Battle.class);
        verify(battleRepository).save(battleArgument.capture());

        Battle battle = battleArgument.getValue();
        List<BattleParticipant> battleParticipants = battle.getParticipants();
        assertEquals(battleParticipants.size(), 1);
        assertEquals(battleParticipants.stream().findFirst().get().getCharacter(), character);

        // Assert process variables
        verify(delegateExecution).setVariable("attackerCount", 1);
        verify(delegateExecution, never()).setVariable(eq("gameFull"), anyBoolean());
    }


    @Test
    public void testDefenderJoinedAndGameFullSuccess() throws Exception {
        when(map.getTotalSlots()).thenReturn(2);
        when(character.getFaction()).thenReturn(Faction.CYBRAN);
        when(delegateExecution.getVariable("attackerCount")).thenReturn(1);
        when(delegateExecution.getVariable("defenderCount")).thenReturn(0);

        GwCharacter attackerCharacter = mock(GwCharacter.class);
        when(attackerCharacter.getFaction()).thenReturn(Faction.UEF);
        when(attackerCharacter.getId()).thenReturn(UUID.fromString("12345678-1111-1111-1111-111111111111"));
        BattleParticipant attackerParticipant = mock(BattleParticipant.class);
        when(attackerParticipant.getCharacter()).thenReturn(attackerCharacter);
        List<BattleParticipant> participantList = new ArrayList<>();
        participantList.add(attackerParticipant);
        when(battle.getParticipants()).thenReturn(participantList);

        task.execute(delegateExecution);

        // Assert input taken from bpmn process
        verify(delegateExecution).getVariable("battle");
        verify(delegateExecution).getVariable("planet");
        verify(delegateExecution).getVariable("requestCharacter");
        verify(delegateExecution).getVariable("defenderCount");

        // Assert validation methods called correctly
        verify(validationHelper).validateCharacterInBattle(character, battle, false);
        verify(validationHelper).validateCharacterFreeForGame(character);
        verify(validationHelper).validateOpenSlotForCharacter(battle, BattleRole.DEFENDER);

        // Assert attributes of battle
        ArgumentCaptor<Battle> battleArgument = ArgumentCaptor.forClass(Battle.class);
        verify(battleRepository).save(battleArgument.capture());

        Battle battle = battleArgument.getValue();
        List<BattleParticipant> battleParticipants = battle.getParticipants();
        assertEquals(battleParticipants.size(), 2);
        assertEquals(battleParticipants.stream()
                .filter(battleParticipant -> battleParticipant.getCharacter().getId() == character.getId())
                .count(), 1);

        // Assert process variables
        verify(delegateExecution).setVariable("defenderCount", 1);
        verify(delegateExecution).setVariable("gameFull", true);
    }

    @Test(expected = BpmnError.class)
    public void testValidateCharacterInBattleThrowsException() throws Exception {
        doThrow(new BpmnError("validationHelper.validateCharacterInBattle"))
                .when(validationHelper).validateCharacterInBattle(character, battle, false);

        task.execute(delegateExecution);
    }

    @Test(expected = BpmnError.class)
    public void testValidateCharacterFreeForGameThrowsException() throws Exception {
        doThrow(new BpmnError("validationHelper.validateCharacterFreeForGame"))
                .when(validationHelper).validateCharacterFreeForGame(character);

        task.execute(delegateExecution);
    }

    @Test(expected = BpmnError.class)
    public void testValidateOpenSlotForCharacterThrowsException() throws Exception {
        doThrow(new BpmnError("validationHelper.validateOpenSlotForCharacter"))
                .when(validationHelper).validateOpenSlotForCharacter(battle, BattleRole.ATTACKER);

        task.execute(delegateExecution);
    }
}