package com.faforever.gw.model.service;

import com.faforever.gw.model.CreditJournalEntry;
import com.faforever.gw.model.CreditJournalEntryReason;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Reinforcement;
import com.faforever.gw.model.repository.ReinforcementsGroupRepository;
import com.faforever.gw.model.repository.ReinforcementsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ReinforcementsService {

	private ReinforcementsRepository reinforcementsRepository;
	private ReinforcementsGroupRepository reinforcementsGroupRepository;

	@Inject
	public ReinforcementsService(ReinforcementsRepository reinforcementsRepository, ReinforcementsGroupRepository reinforcementsGroupRepository) {
		this.reinforcementsRepository = reinforcementsRepository;
		this.reinforcementsGroupRepository = reinforcementsGroupRepository;
	}

	@Transactional
	public Map<Reinforcement, Integer> getAllReinforcements(GwCharacter character) {
		Map<Reinforcement, Integer> res = new HashMap<>();
		reinforcementsRepository.findAll().forEach(r -> res.put(r, 0));
		character.getCreditJournalList().stream()
				.filter(entry -> entry.getReason() == CreditJournalEntryReason.REINFORCEMENTS)
				.map(CreditJournalEntry::getReinforcementsTransaction)
				.forEach(transaction ->
						res.put(transaction.getReinforcement(), res.get(transaction.getReinforcement()) + transaction.getQuantity())
				);

		return res;
	}

	@Transactional
	public Map<Reinforcement, Integer> getAvailableReinforcements(GwCharacter character) {
		Map<Reinforcement, Integer> res = getAllReinforcements(character);

		character.getReinforcementsGroupList().stream()
				.flatMap(group -> group.getReinforcements().stream())
				.forEach(reinforcement ->
						res.put(reinforcement, res.get(reinforcement) - 1)
				);

		return res;
	}

	//TODO: buy reinforcement
	//TODO: group/ungroup reinforcement
	//TODO: join game with reinforcement
	//TODO: buy defense structure
}
