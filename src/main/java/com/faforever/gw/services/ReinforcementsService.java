package com.faforever.gw.services;

import com.faforever.gw.bpmn.services.GwErrorType;
import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.inbound.BuyDefenseStructureMessage;
import com.faforever.gw.messaging.client.inbound.BuyReinforcementsMessage;
import com.faforever.gw.messaging.client.outbound.AckMessage;
import com.faforever.gw.messaging.client.outbound.DefenseStructureBuiltMessage;
import com.faforever.gw.messaging.client.outbound.ErrorMessage;
import com.faforever.gw.model.*;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.DefenseStructureRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import com.faforever.gw.model.repository.ReinforcementsRepository;
import com.faforever.gw.model.service.CharacterService;
import com.faforever.gw.security.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ReinforcementsService {

	private final ClientMessagingService clientMessagingService;
	private final CharacterService characterService;
	private final UserService userService;
	private final ReinforcementsRepository reinforcementsRepository;
	private final CharacterRepository characterRepository;
	private final DefenseStructureRepository defenseStructureRepository;
	private final PlanetRepository planetRepository;

	@Inject
	public ReinforcementsService(ClientMessagingService clientMessagingService, CharacterService characterService, UserService userService, ReinforcementsRepository reinforcementsRepository, CharacterRepository characterRepository, DefenseStructureRepository defenseStructureRepository, PlanetRepository planetRepository) {
		this.clientMessagingService = clientMessagingService;
		this.characterService = characterService;
		this.userService = userService;
		this.reinforcementsRepository = reinforcementsRepository;
		this.characterRepository = characterRepository;
		this.defenseStructureRepository = defenseStructureRepository;
		this.planetRepository = planetRepository;
	}

	@EventListener
	@Transactional
	public void onBuyReinforcements(BuyReinforcementsMessage message) {
		User user = userService.getUserFromContext();
		GwCharacter character = userService.getActiveCharacter(user);

		if(character == null) {
			sendErrorToUser(user, message.getRequestId(), GwErrorType.NO_ACTIVE_CHARACTER);
			return;
		}

		double creditsAvailable = getAvailableCredits(character);

		Reinforcement reinforcement = reinforcementsRepository.findOne(message.getReinforcementId());
		if(reinforcement == null) {
			sendErrorToUser(user, message.getRequestId(), GwErrorType.REINFORCEMENT_INVALID);
			return;
		}

		if(message.getQuantity() <= 0) {
			sendErrorToUser(user, message.getRequestId(), GwErrorType.NOT_ENOUGH_CREDITS);
			return;
		}

		double cost = reinforcement.getPrice() * (double) message.getQuantity();
		if(cost < creditsAvailable) {
			sendErrorToUser(user, message.getRequestId(), GwErrorType.NOT_ENOUGH_CREDITS);
			return;
		}

		CreditJournalEntry creditJournalEntry = new CreditJournalEntry(character, null, CreditJournalEntryReason.REINFORCEMENTS, (-1) * (cost));
		ReinforcementsTransaction reinforcementsTransaction = new ReinforcementsTransaction(character, null, creditJournalEntry, reinforcement, message.getQuantity());

		character.getCreditJournalList().add(creditJournalEntry);
		character.getReinforcementsTransactionList().add(reinforcementsTransaction);
		characterRepository.save(character);

		sendAckToUser(user, message.getRequestId());
	}

	@EventListener
	@Transactional
	public void onBuyDefenseStructure(BuyDefenseStructureMessage message) {
		User user = userService.getUserFromContext();
		GwCharacter character = userService.getActiveCharacter(user);

		if(character == null) {
			sendErrorToUser(user, message.getRequestId(), GwErrorType.NO_ACTIVE_CHARACTER);
			return;
		}

		double creditsAvailable = getAvailableCredits(character);

		DefenseStructure defenseStructure = defenseStructureRepository.findOne(message.getDefenseStructureId());
		if(defenseStructure == null) {
			sendErrorToUser(user, message.getRequestId(), GwErrorType.STRUCTURE_INVALID);
			return;
		}

		double cost = defenseStructure.getPrize();
		if(cost < creditsAvailable) {
			sendErrorToUser(user, message.getRequestId(), GwErrorType.NOT_ENOUGH_CREDITS);
			return;
		}

		Planet planet = planetRepository.findOne(message.getPlanetId());
		if(planet == null) {
			sendErrorToUser(user, message.getRequestId(), GwErrorType.PLANET_DOES_NOT_EXIST);
			return;
		}

		if(planet.getCurrentOwner() != character.getFaction()) {
			sendErrorToUser(user, message.getRequestId(), GwErrorType.PLANET_NOT_CONTROLLED);
			return;
		}

		//TODO: limit defense structures on planet

		CreditJournalEntry creditJournalEntry = new CreditJournalEntry(character, null, CreditJournalEntryReason.DEFENSE_STRUCTURE, cost);
		character.getCreditJournalList().add(creditJournalEntry);
		characterRepository.save(character);

		planet.getDeployedDefenseStructureList().add(new DeployedDefenseStructure(defenseStructure, planet, planet.getCurrentOwner(), creditJournalEntry));
		planetRepository.save(planet);

		clientMessagingService.sendToPublic(new DefenseStructureBuiltMessage(defenseStructure.getId(), planet.getId(), planet.getCurrentOwner()));
	}



	@Transactional
	public double getAvailableCredits(GwCharacter character) {
		return character.getCreditJournalList().stream()
				.mapToDouble(CreditJournalEntry::getAmount).sum();
	}

	@Transactional
	public void addIncome(GwCharacter character, double amount) {
		character.getCreditJournalList().add(new CreditJournalEntry(
				character,
				null,
				CreditJournalEntryReason.REGULAR_INCOME,
				amount
		));
		characterRepository.save(character);
	}

	@Transactional
	public java.util.Map<Reinforcement, Integer> getOwnReinforcements(GwCharacter character) {
		java.util.Map<Reinforcement, Integer> res = new HashMap<>();
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
	public java.util.Map<Reinforcement, Integer> getAvailableOwnReinforcements(GwCharacter character) {
		Map<Reinforcement, Integer> res = getOwnReinforcements(character);

		character.getReinforcementsGroupList().stream()
				.flatMap(group -> group.getReinforcements().stream())
				.forEach(entry ->
						res.put(entry.getReinforcement(), res.get(entry.getReinforcement()) - entry.getQuantity())
				);

		return res;
	}

	//TODO: group/ungroup reinforcement
	//TODO: buy defense structure
	//TODO: join game with reinforcement


	//TODO: duplicate code (e.g. AdminService)
	private void sendErrorToUser(User user, UUID requestId, GwErrorType errorType) {
		clientMessagingService.sendToUser(new ErrorMessage(requestId, errorType.getErrorCode(), errorType.getErrorMessage()), user);
	}

	private void sendAckToUser(User user, UUID requestId) {
		clientMessagingService.sendToUser(new AckMessage(requestId), user);
	}
}
