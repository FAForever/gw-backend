package com.faforever.gw.task.planetary_assault;

import com.faforever.gw.bpmn.task.planetary_assault.InitiateAssaultTask;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.BattleParticipant;
import com.faforever.gw.model.BattleStatus;
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

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
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
        when(planetRepository.getOne(any(UUID.class))).thenReturn(planet);

        when(character.getFaction())
                .thenReturn(Faction.UEF);
        when(planet.getCurrentOwner())
                .thenReturn(Faction.CYBRAN);

        task = new InitiateAssaultTask(mock(EntityManager.class), characterRepository, planetRepository, battleRepository, validationHelper);

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

    @Test
    public void testValidateAssaultOnPlanetThrowsException() throws Exception {
        doThrow(new BpmnError("validationHelper.validateAssaultOnPlanet"))
                .when(validationHelper).validateAssaultOnPlanet(character, planet);

        assertThrows(BpmnError.class, () -> task.execute(delegateExecution));
    }

    @Test
    public void testValidateCharacterFreeForGameThrowsException() throws Exception {
        doThrow(new BpmnError("validationHelper.validateCharacterFreeForGame"))
                .when(validationHelper).validateCharacterFreeForGame(character);

        assertThrows(BpmnError.class, () -> task.execute(delegateExecution));
    }

}