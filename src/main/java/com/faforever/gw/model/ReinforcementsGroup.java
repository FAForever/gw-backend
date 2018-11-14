package com.faforever.gw.model;

import com.yahoo.elide.annotation.Include;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Entity
@Include(rootLevel = true)
@Table(name = "gw_reinforcements_group")
public class ReinforcementsGroup implements Serializable {

	private UUID id;
	private Character character;
	private ReinforcementsType type;
	private List<Reinforcement> reinforcements = new ArrayList<>();

	public ReinforcementsGroup(ReinforcementsType type, List<Reinforcement> reinforcements) {
		this.type = type;
		this.reinforcements = reinforcements;
	}

	@Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
	public UUID getId() {
		return id;
	}

	@ManyToOne
	@JoinColumn(name = "fk_character")
	public Character getCharacter() {
		return character;
	}

	@Column(name = "type")
	public ReinforcementsType getType() {
		return type;
	}

	//TODO: does this work?
	@ManyToMany
	@JoinTable(name = "gw_reinforcements_group_reinforcements",//TODO
			joinColumns = @JoinColumn(name = "fk_reinforcement_group"),
			inverseJoinColumns = @JoinColumn(name = "fk_reinforcement"))
	public List<Reinforcement> getReinforcements() {
		return reinforcements;
	}
}
