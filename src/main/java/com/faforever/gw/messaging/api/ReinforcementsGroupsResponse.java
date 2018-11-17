package com.faforever.gw.messaging.api;

import com.faforever.gw.model.Reinforcement;
import com.faforever.gw.model.ReinforcementsGroup;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class ReinforcementsGroupsResponse extends ApiResponse {

	private final List<Group> groups;
	private final Map<UUID, Integer> freeReinforcements;

	public ReinforcementsGroupsResponse(List<ReinforcementsGroup> reinforcementsGroups, Map<Reinforcement, Integer> availableReinforcements) {
		this.groups = reinforcementsGroups.stream().map(Group::new).collect(Collectors.toList());
		this.freeReinforcements = ReinforcementsResponse.reinforcementsMapToId(availableReinforcements);
	}

	//TODO: ReinforcementsGroupsResponseGroup is a bit long ;)
	@Getter
	public class Group {
		private final UUID id;
		private final Map<UUID, Integer> reinforcements;

		public Group(ReinforcementsGroup group) {
			this.id = group.getId();
			this.reinforcements = group.getReinforcements().stream().collect(Collectors.toMap(
					r -> r.getReinforcement().getId(),
					r -> r.getQuantity()
			));
		}
	}
}
