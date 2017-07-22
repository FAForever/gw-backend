package com.faforever.gw.task.planetary_assault;

import com.faforever.gw.bpmn.task.planetary_assault.CloseAssaultTask;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.BattleStatus;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CloseAssaultTest {
    @Mock
    private DelegateExecution delegateExecution;
    @Mock
    private BattleRepository battleRepository;
    @Mock
    private PlanetRepository planetRepository;
    @Mock
    private Battle battle;
    @Mock
    private Planet planet;

    private CloseAssaultTask task;

    @Before
    public void setUp() throws Exception {
        when(delegateExecution.getProcessInstance()).thenReturn(delegateExecution);
        when(delegateExecution.getBusinessKey()).thenReturn("test");

        when(delegateExecution.getVariable("battle"))
                .thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));

        when(battleRepository.findOne(any(UUID.class))).thenReturn(battle);
        when(planetRepository.findOne(any(UUID.class))).thenReturn(planet);
        when(battle.getAttackingFaction()).thenReturn(Faction.UEF);
        when(battle.getDefendingFaction()).thenReturn(Faction.CYBRAN);

        task = new CloseAssaultTask(battleRepository, planetRepository);
    }

    @Test
    public void testWinnerAttacker() throws Exception {
        when(delegateExecution.getVariable("winner")).thenReturn("attacker");

        task.execute(delegateExecution);

        // Assert input taken from bpmn process
        verify(delegateExecution, atLeastOnce()).getVariable("battle");

        // Assert attributes of battle
        verify(battle).setEndedAt(any());
        verify(battle).setStatus(BattleStatus.FINISHED);
        verify(battle).setWinningFaction(Faction.UEF);
        verify(planet).setCurrentOwner(Faction.UEF);
    }

    @Test
    public void testWinnerDefender() throws Exception {
        when(delegateExecution.getVariable("winner")).thenReturn("defender");

        task.execute(delegateExecution);

        // Assert input taken from bpmn process
        verify(delegateExecution, atLeastOnce()).getVariable("battle");

        // Assert attributes of battle
        verify(battle).setEndedAt(any());
        verify(battle).setStatus(BattleStatus.FINISHED);
        verify(battle).setWinningFaction(Faction.CYBRAN);
        verify(planet, never()).setCurrentOwner(any());
    }

}