package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import com.faforever.gw.model.Faction;
import lombok.Data;

import java.util.UUID;

@Data
public class DefenseStructureBuiltMessage extends OutboundClientMessage {
	private UUID deployedDefenseStrucutre;
	private UUID planet;
	private Faction faction;

	public DefenseStructureBuiltMessage(UUID deployedDefenseStrucutre, UUID planet, Faction faction) {
		this.deployedDefenseStrucutre = deployedDefenseStrucutre;
		this.planet = planet;
		this.faction = faction;
	}

	@Override
	public Audience getAudience() {
		return Audience.PUBLIC;
	}
}