package com.faforever.gw.api;


import com.faforever.gw.api.error.ApiException;
import com.faforever.gw.api.messages.CreditsResponse;
import com.faforever.gw.api.messages.ReinforcementsGroupsResponse;
import com.faforever.gw.api.messages.ReinforcementsResponse;
import com.faforever.gw.bpmn.services.GwErrorType;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Reinforcement;
import com.faforever.gw.model.ReinforcementsGroup;
import com.faforever.gw.security.GwUserRegistry;
import com.faforever.gw.security.User;
import com.faforever.gw.services.ReinforcementsService;
import com.faforever.gw.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = ReinforcementsController.PATH_PREFIX)
public class ReinforcementsController {

	public static final String PATH_PREFIX = "/reinforcements";
	private static final String JSON_API_MEDIA_TYPE = "application/vnd.api+json";

	private final ReinforcementsService reinforcementsService;
	private final UserService userService;
	private final GwUserRegistry userRegistry;

	public ReinforcementsController(ReinforcementsService reinforcementsService, UserService userService, GwUserRegistry userRegistry) {
		this.reinforcementsService = reinforcementsService;
		this.userService = userService;
		this.userRegistry = userRegistry;
	}

	@RequestMapping(
			method = RequestMethod.GET,
			produces = JSON_API_MEDIA_TYPE,
			value = {"/fakeAuthorization"})
	public Object fakeAuthorize(@RequestParam long fafUserID) {
		Optional<User> user = userService.getOnlineUserByFafId(fafUserID);

		if (user.isPresent()) {

			return ResponseEntity.status(HttpStatus.OK);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(
		method = RequestMethod.GET,
		produces = JSON_API_MEDIA_TYPE,
		value = {"/availableCredits"})
	@PreAuthorize("hasRole('USER')")
//	@Transactional(readOnly = true)
	public CreditsResponse getAvailableCredits() {
		User user = userService.getUserFromContext();
		GwCharacter character = userService.getActiveCharacter(user);

		if(character == null) {
			throw new ApiException(GwErrorType.NO_ACTIVE_CHARACTER);
		}

		return new CreditsResponse((int) reinforcementsService.getAvailableCredits(character));
	}


	@RequestMapping(
			method = RequestMethod.GET,
			produces = JSON_API_MEDIA_TYPE,
			value = {"/ownReinforcements"})
	@PreAuthorize("hasRole('USER')")
//	@Transactional(readOnly = true)
	public ReinforcementsResponse getOwnReinforcements() {
		User user = userService.getUserFromContext();
		GwCharacter character = userService.getActiveCharacter(user);

		if(character == null) {
			throw new ApiException(GwErrorType.NO_ACTIVE_CHARACTER);
		}

		Map<Reinforcement, Integer> reinforcements = reinforcementsService.getOwnReinforcements(character);
		return new ReinforcementsResponse(reinforcements);
	}


	//Get all reinforcements via elide
	//Get defense structures via elide

	@RequestMapping(
			method = RequestMethod.GET,
			produces = JSON_API_MEDIA_TYPE,
			value = {"/reinforcementGroups"})
	@PreAuthorize("hasRole('USER')")
//	@Transactional(readOnly = true)
	public ReinforcementsGroupsResponse getReinforcementGroups() {
		User user = userService.getUserFromContext();
		GwCharacter character = userService.getActiveCharacter(user);

		if(character == null) {
//			return ResponseEntity.badRequest().body(GwErrorType.NO_ACTIVE_CHARACTER.getErrorMessage());
		}

		List<ReinforcementsGroup> reinforcementsGroups = character.getReinforcementsGroupList();
		Map<Reinforcement, Integer> availableOwnReinforcements = reinforcementsService.getAvailableOwnReinforcements(character);
		return new ReinforcementsGroupsResponse(reinforcementsGroups, availableOwnReinforcements);
	}
}
