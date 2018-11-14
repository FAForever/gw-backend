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
@Table(name = "gw_defense_structure")
public class DefenseStructure {
	private UUID id;
	private Unit unit;
	private Planet planet;
	private Faction faction;
	private CreditJournalEntry creditJournalEntry;

	public DefenseStructure(Unit unit, Planet planet, Faction faction, CreditJournalEntry creditJournalEntry) {
		this.unit = unit;
		this.planet = planet;
		this.faction = faction;
		this.creditJournalEntry = creditJournalEntry;
	}

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	public UUID getId() {
		return id;
	}

	@ManyToOne
	@JoinColumn(name = "fk_unit")
	public Unit getUnit() {
		return unit;
	}

	@ManyToOne
	@JoinColumn(name = "fk_planet")
	public Planet getPlanet() {
		return planet;
	}

	@Column(name = "faction")
	public Faction getFaction() {
		return faction;
	}

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "defenseStructure")
	public CreditJournalEntry getCreditJournalEntry() {
		return creditJournalEntry;
	}
}
