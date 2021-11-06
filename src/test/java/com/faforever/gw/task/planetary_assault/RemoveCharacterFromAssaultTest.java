package com.faforever.gw.task.planetary_assault;

import com.faforever.gw.bpmn.task.planetary_assault.RemoveCharacterFromAssaultTask;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.BattleParticipant;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.ValidationHelper;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
    public void setUp() throws Exception {
        when(delegateExecution.getProcessInstance()).thenReturn(delegateExecution);
        when(delegateExecution.getBusinessKey()).thenReturn("test");

        lenient().when(delegateExecution.getVariable("requestCharacter"))
                .thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        lenient().when(delegateExecution.getVariable("planet"))
                .thenReturn(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        lenient().when(delegateExecution.getVariable("battle"))
                .thenReturn(UUID.fromString("33333333-3333-3333-3333-333333333333"));

        when(characterRepository.getOne(any(UUID.class))).thenReturn(character);
        lenient().when(character.getId()).thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        when(battleRepository.getOne(any(UUID.class))).thenReturn(battle);
        lenient().when(planetRepository.getOne(any(UUID.class))).thenReturn(planet);

        lenient().when(battle.getAttackingFaction()).thenReturn(Faction.UEF);
        lenient().when(battle.getDefendingFaction()).thenReturn(Faction.CYBRAN);
        ArrayList<BattleParticipant> battleParticipants = new ArrayList<>();
        lenient().when(battle.getParticipants()).thenReturn(battleParticipants);


        task = new RemoveCharacterFromAssaultTask(characterRepository, battleRepository, validationHelper);


        // default setUp
        lenient().when(character.getFaction()).thenReturn(Faction.UEF);
        lenient().when(delegateExecution.getVariable("attackerCount")).thenReturn(1);
    }

    @Test
    public void testAttackerLeftSuccess() throws Exception {
        // Setup attacker
        BattleParticipant attackerParticipant = mock(BattleParticipant.class);
        when(attackerParticipant.getCharacter()).thenReturn(character);
        battle.getParticipants().add(attackerParticipant);

        // Setup defender
        GwCharacter defenderCharacter = mock(GwCharacter.class);
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

    @Test
    public void testValidateCharacterInBattleThrowsException() throws Exception {
        doThrow(new BpmnError("validationHelper.validateCharacterInBattle"))
                .when(validationHelper).validateCharacterInBattle(character, battle, true);

        assertThrows(BpmnError.class, () -> task.execute(delegateExecution));
    }

}