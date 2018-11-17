package com.faforever.gw.model;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Entity
@Table(name = "gw_reinforcements_group_entry")
public class ReinforcementsGroupEntry {
	private UUID id;
	private ReinforcementsGroup group;
	private Reinforcement reinforcement;
	private int quantity;

	public ReinforcementsGroupEntry(ReinforcementsGroup group, Reinforcement reinforcement, int quantity) {
		this.group = group;
		this.reinforcement = reinforcement;
		this.quantity = quantity;
	}

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	public UUID getId() {
		return id;
	}

	@ManyToOne
	@JoinColumn(name = "fk_group")
	public ReinforcementsGroup getGroup() {
		return group;
	}

	@ManyToOne
	@JoinColumn(name = "fk_reinforcement")
	public Reinforcement getReinforcement() {
		return reinforcement;
	}

	@Column(name = "quantity")
	public int getQuantity() {
		return quantity;
	}
}
