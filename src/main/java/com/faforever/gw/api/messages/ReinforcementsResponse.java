package com.faforever.gw.api.messages;

import com.faforever.gw.model.Reinforcement;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class ReinforcementsResponse {
	private final Map<UUID, Integer> reinforcements;

	public ReinforcementsResponse(Map<Reinforcement, Integer> reinforcements) {
		this.reinforcements = reinforcementsMapToId(reinforcements);
	}

	public static Map<UUID, Integer> reinforcementsMapToId(Map<Reinforcement, Integer> reinforcements) {
		return reinforcements.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue));
	}
}
