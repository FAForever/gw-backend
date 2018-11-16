package com.faforever.gw.services;

import com.faforever.gw.bpmn.services.GwErrorType;
import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.inbound.BuyReinforcementsMessage;
import com.faforever.gw.messaging.client.outbound.AckMessage;
import com.faforever.gw.messaging.client.outbound.ErrorMessage;
import com.faforever.gw.model.*;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.ReinforcementsRepository;
import com.faforever.gw.model.service.CharacterService;
import com.faforever.gw.security.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.UUID;

@Service
@Slf4j
public class ReinforcementsService {

	private final ClientMessagingService clientMessagingService;
	private final CharacterService characterService;
	private final UserService userService;
	private final ReinforcementsRepository reinforcementsRepository;
	private final CharacterRepository characterRepository;

	@Inject
	public ReinforcementsService(ClientMessagingService clientMessagingService, CharacterService characterService, UserService userService, ReinforcementsRepository reinforcementsRepository, CharacterRepository characterRepository) {
		this.clientMessagingService = clientMessagingService;
		this.characterService = characterService;
		this.userService = userService;
		this.reinforcementsRepository = reinforcementsRepository;
		this.characterRepository = characterRepository;
	}

	@EventListener
	@Transactional
	public void onBuyReinforcements(BuyReinforcementsMessage message) {
		User user = userService.getUserFromContext();
		GwCharacter character = userService.getActiveCharacter(user);
		double creditsAvailable = characterService.getAvailableCredits(character);

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


	//TODO: duplicate code (e.g. AdminService)
	private void sendErrorToUser(User user, UUID requestId, GwErrorType errorType) {
		clientMessagingService.sendToUser(new ErrorMessage(requestId, errorType.getErrorCode(), errorType.getErrorMessage()), user);
	}

	private void sendAckToUser(User user, UUID requestId) {
		clientMessagingService.sendToUser(new AckMessage(requestId), user);
	}
}
