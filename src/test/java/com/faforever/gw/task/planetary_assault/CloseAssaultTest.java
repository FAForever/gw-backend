package com.faforever.gw.task.planetary_assault;

import com.faforever.gw.bpmn.task.planetary_assault.CloseAssaultTask;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.BattleStatus;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
    public void setUp() throws Exception {
        when(delegateExecution.getProcessInstance()).thenReturn(delegateExecution);
        when(delegateExecution.getBusinessKey()).thenReturn("test");

        when(delegateExecution.getVariable("planet"))
                .thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        when(delegateExecution.getVariable("battle"))
                .thenReturn(UUID.fromString("22222222-2222-2222-2222-222222222222"));

        when(battleRepository.getOne(any(UUID.class))).thenReturn(battle);
        when(planetRepository.getOne(any(UUID.class))).thenReturn(planet);
        lenient().when(battle.getAttackingFaction()).thenReturn(Faction.UEF);
        lenient().when(battle.getDefendingFaction()).thenReturn(Faction.CYBRAN);

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