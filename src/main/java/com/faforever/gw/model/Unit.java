package com.faforever.gw.model;

import com.yahoo.elide.annotation.Include;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Entity
@Include(rootLevel = true)
@Table(name = "gw_unit")
public class Unit {
	private UUID id;
	private String faUid;
	private String name;
	private Faction faction;
	private TechLevel techLevel;

	public Unit(String faUid, String name, Faction faction, TechLevel techLevel) {
		this.faUid = faUid;
		this.name = name;
		this.faction = faction;
		this.techLevel = techLevel;
	}

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	public UUID getId() {
		return id;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	@Column(name = "fa_uid")
	public String getFaUid() {
		return faUid;
	}

	@Column(name = "faction")
	public Faction getFaction() {
		return faction;
	}

	@Column(name = "tech_level")
	public TechLevel getTechLevel() {
		return techLevel;
	}
}
