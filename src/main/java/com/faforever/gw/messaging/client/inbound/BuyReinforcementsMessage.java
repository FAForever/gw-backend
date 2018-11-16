package com.faforever.gw.messaging.client.inbound;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class BuyReinforcementsMessage extends InboundClientMessage {
	private UUID reinforcementId;
	private int quantity;
}
