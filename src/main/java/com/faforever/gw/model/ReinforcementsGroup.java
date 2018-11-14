package com.faforever.gw.model;

import com.yahoo.elide.annotation.Include;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Entity
@Include(rootLevel = true)
@Table(name = "gw_reinforcements_group")
public class ReinforcementsGroup {

	private UUID id;
	private ReinforcementsGroupType type;
	private List<Unit> units = new ArrayList<>();
	private List<PassiveItem> items = new ArrayList<>();
	private boolean called;
	private long delay;

	public ReinforcementsGroup(ReinforcementsGroupType type, List<Unit> units, List<PassiveItem> items, boolean called, long delay) {
		this.type = type;
		this.units = units;
		this.items = items;
		this.called = called;
		this.delay = delay;
	}

	@Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
	public UUID getId() {
		return id;
	}

	@Column(name = "type")
	public ReinforcementsGroupType getType() {
		return type;
	}

	@ManyToMany
	@JoinTable(name = "gw_reinforcements_group_unit",
			joinColumns = @JoinColumn(name = "fk_reinforcement_group"),
			inverseJoinColumns = @JoinColumn(name = "fk_unit"))
	public List<Unit> getUnits() {
		return units;
	}

	@ManyToMany
	@JoinTable(name = "gw_reinforcements_group_item",
			joinColumns = @JoinColumn(name = "fk_reinforcement_group"),
			inverseJoinColumns = @JoinColumn(name = "fk_item"))
	public List<PassiveItem> getItems() {
		return items;
	}

	@Column(name = "called")
	public boolean isCalled() {
		return called;
	}

	@Column(name = "delay")
	public long getDelay() {
		return delay;
	}
}
