package com.faforever.gw.api;


import com.faforever.gw.bpmn.services.GwErrorType;
import com.faforever.gw.messaging.api.CreditsResponse;
import com.faforever.gw.messaging.api.ReinforcementsGroupsResponse;
import com.faforever.gw.messaging.api.ReinforcementsResponse;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Reinforcement;
import com.faforever.gw.model.ReinforcementsGroup;
import com.faforever.gw.security.User;
import com.faforever.gw.services.ReinforcementsService;
import com.faforever.gw.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

//TODO: this is infact not REST
@RestController
@RequestMapping(path = ReinforcementsController.PATH_PREFIX)
public class ReinforcementsController {

	public static final String PATH_PREFIX = "/data";
	private static final String JSON_API_MEDIA_TYPE = "application/vnd.api+json";

	private final ReinforcementsService reinforcementsService;
	private final UserService userService;

	public ReinforcementsController(ReinforcementsService reinforcementsService, UserService userService) {
		this.reinforcementsService = reinforcementsService;
		this.userService = userService;
	}


	@RequestMapping(
		method = RequestMethod.GET,
		produces = JSON_API_MEDIA_TYPE,
		value = {"/availableCredits"})
	@PreAuthorize("hasRole('USER')")
//	@Transactional(readOnly = true)
	public ResponseEntity<String> getAvailableCredits() {
		User user = userService.getUserFromContext();
		GwCharacter character = userService.getActiveCharacter(user);

		if(character == null) {
			return ResponseEntity.badRequest().body(GwErrorType.NO_ACTIVE_CHARACTER.getErrorMessage());
		}

		//TODO: auto convert?
		return ResponseEntity.ok(new CreditsResponse((int) reinforcementsService.getAvailableCredits(character)).toJson());
	}


	@RequestMapping(
			method = RequestMethod.GET,
			produces = JSON_API_MEDIA_TYPE,
			value = {"/ownReinforcements"})
	@PreAuthorize("hasRole('USER')")
//	@Transactional(readOnly = true)
	public ResponseEntity<String> getOwnReinforcements() {
		User user = userService.getUserFromContext();
		GwCharacter character = userService.getActiveCharacter(user);

		if(character == null) {
			return ResponseEntity.badRequest().body(GwErrorType.NO_ACTIVE_CHARACTER.getErrorMessage());
		}

		Map<Reinforcement, Integer> reinforcements = reinforcementsService.getOwnReinforcements(character);
		return ResponseEntity.ok(new ReinforcementsResponse(reinforcements).toJson());
	}


	//Get all reinforcements via elide
	//Get defense structures via elide

	@RequestMapping(
			method = RequestMethod.GET,
			produces = JSON_API_MEDIA_TYPE,
			value = {"/reinforcementGroups"})
	@PreAuthorize("hasRole('USER')")
//	@Transactional(readOnly = true)
	public ResponseEntity<String> getReinforcementGroups() {
		User user = userService.getUserFromContext();
		GwCharacter character = userService.getActiveCharacter(user);

		if(character == null) {
			return ResponseEntity.badRequest().body(GwErrorType.NO_ACTIVE_CHARACTER.getErrorMessage());
		}

		List<ReinforcementsGroup> reinforcementsGroups = character.getReinforcementsGroupList();
		Map<Reinforcement, Integer> availableOwnReinforcements = reinforcementsService.getAvailableOwnReinforcements(character);
		return ResponseEntity.ok(new ReinforcementsGroupsResponse(reinforcementsGroups, availableOwnReinforcements).toJson());
	}



//	@RequestMapping(
//			method = RequestMethod.POST,
//			produces = JSON_API_MEDIA_TYPE,
//			value = {"/reinforcementGroups"})
//	@PreAuthorize("hasRole('USER')")
//	//TODO: UUID as param?
//	public ResponseEntity<String> buyReinforcements(@RequestParam("id") UUID id, @RequestParam("quantity") int quantity) {
//		User user = userService.getUserFromContext();
//		GwCharacter character = userService.getActiveCharacter(user);
//
//		if(character == null) {
//			return ResponseEntity.badRequest().body(GwErrorType.NO_ACTIVE_CHARACTER.getErrorMessage());
//		}
//
//
//	}
}
