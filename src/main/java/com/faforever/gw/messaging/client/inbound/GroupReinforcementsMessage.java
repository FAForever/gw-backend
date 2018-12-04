package com.faforever.gw.messaging.client.inbound;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class GroupReinforcementsMessage extends InboundClientMessage {
	private UUID groupId;//group to change or to be deleted
	private final Map<UUID, Integer> reinforcements;
}
