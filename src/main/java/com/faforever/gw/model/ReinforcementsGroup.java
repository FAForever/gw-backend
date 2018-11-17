package com.faforever.gw.model;

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
@Table(name = "gw_reinforcements_group")
public class ReinforcementsGroup implements Serializable {

	private UUID id;
	private Character character;
	private ReinforcementsType type;
	private List<ReinforcementsGroupEntry> reinforcements = new ArrayList<>();

	public ReinforcementsGroup(ReinforcementsType type, List<ReinforcementsGroupEntry> reinforcements) {
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
//	@ManyToMany
//	@JoinTable(name = "gw_reinforcements_group_reinforcements",//TODO
//			joinColumns = @JoinColumn(name = "fk_reinforcement_group"),
//			inverseJoinColumns = @JoinColumn(name = "fk_reinforcement"))
//	public List<Reinforcement> getReinforcements() {
//		return reinforcements;
//	}


	public List<ReinforcementsGroupEntry> getReinforcements() {
		return reinforcements;
	}
}
