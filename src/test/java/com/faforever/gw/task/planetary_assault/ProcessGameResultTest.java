package com.faforever.gw.task.planetary_assault;

import com.faforever.gw.bpmn.task.planetary_assault.ProcessGameResultTask;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.GameResult;
import com.faforever.gw.model.service.BattleService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProcessGameResultTest {
    @Mock
    private DelegateExecution delegateExecution;
    @Mock
    private BattleService battleService;
    @Mock
    private GameResult gameResult;

    private ProcessGameResultTask task;

    @BeforeEach
    public void setUp() throws Exception {
        task = new ProcessGameResultTask(battleService);

        when(delegateExecution.getProcessInstance()).thenReturn(delegateExecution);
        when(delegateExecution.getBusinessKey()).thenReturn("test");

        when(delegateExecution.getVariable("attackingFaction")).thenReturn(Faction.UEF);
        when(delegateExecution.getVariable("gameResult")).thenReturn(gameResult);
        when(gameResult.getWinner()).thenReturn(Faction.UEF);
    }

    @Test
    public void executeTest() throws Exception {
        task.execute(delegateExecution);

        verify(battleService).processGameResult(gameResult);
    }
}