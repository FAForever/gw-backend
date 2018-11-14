package com.faforever.gw.model;

import com.yahoo.elide.annotation.Include;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Entity
@Include(rootLevel = true)
@Table(name = "gw_reinforcement")
public class Reinforcement implements Serializable {

	private UUID id;
	private ReinforcementsType type;
	private Unit unit;
	private PassiveItem item;
	private long delay;
	private float price;

	public Reinforcement(ReinforcementsType type, Unit unit, PassiveItem item, long delay, float price) {
		this.type = type;
		this.unit = unit;
		this.item = item;
		this.delay = delay;
		this.price = price;
	}

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	public UUID getId() {
		return id;
	}

	@Column(name = "type")
	public ReinforcementsType getType() {
		return type;
	}

	@ManyToOne
	@JoinColumn(name = "fk_unit")
	public Unit getUnit() {
		return unit;
	}

	@ManyToOne
	@JoinColumn(name = "fk_item")
	public PassiveItem getItem() {
		return item;
	}

	@Column(name = "delay")
	public long getDelay() {
		return delay;
	}

	@Column(name = "price")
	public float getPrice() {
		return price;
	}
}
