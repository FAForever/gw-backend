package com.faforever.gw.task.calculate_promotions;

import com.faforever.gw.bpmn.task.calculate_promotions.CheckAndPerformPromotionTask;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Rank;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.RankRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CheckAndPerformPromotionTest {
    @Mock
    GwCharacter character;
    @Mock
    Rank nextRank;
    private CheckAndPerformPromotionTask task;
    @Mock
    private DelegateExecution delegateExecution;
    @Mock
    private CharacterRepository characterRepository;
    @Mock
    private RankRepository rankRepository;

    @Before
    public void setUp() throws Exception {
        when(delegateExecution.getProcessInstance()).thenReturn(delegateExecution);
        when(delegateExecution.getBusinessKey()).thenReturn("test");

        when(delegateExecution.getVariable("character"))
                .thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));

        task = new CheckAndPerformPromotionTask(characterRepository, rankRepository);

        when(character.getRank()).thenReturn(nextRank);
        when(nextRank.getLevel()).thenReturn(5);

        when(characterRepository.findOne(any(UUID.class))).thenReturn(character);
    }

    @Test
    public void testPromotion() throws Exception {
        when(rankRepository.findNextRank(any(), any())).thenReturn(Optional.of(nextRank));

        task.execute(delegateExecution);

        // Assert input taken from bpmn process
        verify(delegateExecution).getVariable("character");

        // Assert changes on character
        ArgumentCaptor<Rank> rankArgument = ArgumentCaptor.forClass(Rank.class);
        verify(character).setRank(rankArgument.capture());

        Rank newRank = rankArgument.getValue();
        assertEquals(nextRank, newRank);

        // Assert process variables
        verify(delegateExecution).setVariable("rankAvailable", true);
        verify(delegateExecution, never()).setVariable("newRank", nextRank);
    }

    @Test
    public void testNoPromotion() throws Exception {

        when(rankRepository.findNextRank(any(), any())).thenReturn(Optional.empty());

        task.execute(delegateExecution);

        // Assert input taken from bpmn process
        verify(delegateExecution).getVariable("character");

        // Assert process variables
        verify(delegateExecution).setVariable("rankAvailable", false);
    }


}