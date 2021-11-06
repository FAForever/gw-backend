package com.faforever.gw.task.calculate_promotions;

import com.faforever.gw.bpmn.task.calculate_promotions.CheckAndPerformPromotionTask;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Rank;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.RankRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
    public void setUp() throws Exception {
        when(delegateExecution.getVariable("character"))
                .thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));

        task = new CheckAndPerformPromotionTask(characterRepository, rankRepository);

        when(character.getRank()).thenReturn(nextRank);
        when(nextRank.getLevel()).thenReturn(5);

        when(characterRepository.getOne(any(UUID.class))).thenReturn(character);
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