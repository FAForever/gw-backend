package com.faforever.gw.messaging.client.inbound;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class BuyDefenseStructureMessage extends InboundClientMessage {
	private UUID defenseStructureId;
	private UUID planetId;

	//TODO: deployed defense structure
}
