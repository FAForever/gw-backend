package com.faforever.gw.bpmn.task.calculate_promotions;


import com.faforever.gw.bpmn.accessors.CalculatePromotionsAccessor;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Rank;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.RankRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@Component
public class CheckAndPerformPromotionTask implements JavaDelegate {
    private final CharacterRepository characterRepository;
    private final RankRepository rankRepository;

    @Inject
    public CheckAndPerformPromotionTask(CharacterRepository characterRepository, RankRepository rankRepository) {
        this.characterRepository = characterRepository;
        this.rankRepository = rankRepository;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.debug("getAvailableRankTask");

        CalculatePromotionsAccessor accessor = CalculatePromotionsAccessor.of(execution);

        GwCharacter character = characterRepository.getOne(accessor.getCharacter_Local());
        Optional<Rank> availableRank = rankRepository.findNextRank(character.getRank().getLevel(), character.getXp());

        if(availableRank.isPresent()) {
            Rank rank = availableRank.get();
            log.info("Character {} will be promoted to rank {}", character.getId(), rank.getLevel());
            character.setRank(rank);
            accessor.setRankAvailable(true)
                    .setNewRank(rank.getLevel());
        }
        else {
            log.debug("Character {} gets no promotion");
            accessor.setRankAvailable(false);
        }
    }
}
