package com.faforever.gw.task.planetary_assault;

import com.faforever.gw.bpmn.services.PlanetaryAssaultService;
import com.faforever.gw.bpmn.task.planetary_assault.CalculateWaitingProgressTask;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.BattleParticipant;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.Map;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.repository.BattleRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CalculateWaitingProgressTest {
    @Mock
    private DelegateExecution delegateExecution;
    @Mock
    private PlanetaryAssaultService planetaryAssaultService;
    @Mock
    private BattleRepository battleRepository;
    @Mock
    private Battle battle;
    @Mock
    private Planet planet;
    @Mock
    private Map map;

    private CalculateWaitingProgressTask task;

    @BeforeEach
    public void setUp() throws Exception {
        when(delegateExecution.getProcessInstance()).thenReturn(delegateExecution);
        when(delegateExecution.getBusinessKey()).thenReturn("test");

        when(delegateExecution.getVariable("battle"))
                .thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        when(delegateExecution.getVariable("waitingProgress")).thenReturn(0.0);

        when(battleRepository.getOne(any(UUID.class))).thenReturn(battle);
        lenient().when(battle.getAttackingFaction()).thenReturn(Faction.UEF);
        lenient().when(battle.getDefendingFaction()).thenReturn(Faction.CYBRAN);
        when(battle.getPlanet()).thenReturn(planet);
        when(planet.getMap()).thenReturn(map);
        when(map.getTotalSlots()).thenReturn(8);

        task = new CalculateWaitingProgressTask(planetaryAssaultService, battleRepository);
    }

    @Test
    public void testZeroProgress() throws Exception {
        when(battle.getParticipants()).thenReturn(new ArrayList<BattleParticipant>());
        when(planetaryAssaultService.calcWaitingProgress(8, 0, 0)).thenReturn(0.0);

        task.execute(delegateExecution);

        // Assert input taken from bpmn process
        verify(delegateExecution, atLeastOnce()).getVariable("battle");
        verify(map).getTotalSlots();

        // Assert attributes of battle
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(delegateExecution).setVariable(eq("waitingProgress"), captor.capture());
        assertEquals(captor.getValue(), 0.0, 0.0001d);
    }

    @Test
    public void testOneAttackerTwoDefenderProgress() throws Exception {
        ArrayList<BattleParticipant> battleParticipants = new ArrayList<>();

        // mock 1 attacker and 2 defender
        BattleParticipant attackerParticipant = mock(BattleParticipant.class);
        when(attackerParticipant.getFaction()).thenReturn(Faction.UEF);
        battleParticipants.add(attackerParticipant);

        BattleParticipant defenderParticipant = mock(BattleParticipant.class);
        when(defenderParticipant.getFaction()).thenReturn(Faction.CYBRAN);
        battleParticipants.add(defenderParticipant);
        battleParticipants.add(defenderParticipant);

        when(battle.getParticipants()).thenReturn(battleParticipants);
        when(planetaryAssaultService.calcWaitingProgress(8, 1, 2)).thenReturn(5.0);

        task.execute(delegateExecution);

        // Assert input taken from bpmn process
        verify(delegateExecution, atLeastOnce()).getVariable("battle");
        verify(map).getTotalSlots();

        // Assert attributes of battle
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(delegateExecution).setVariable(eq("waitingProgress"), captor.capture());
        assertEquals(captor.getValue(), 5.0, 0.0001d);
    }

}