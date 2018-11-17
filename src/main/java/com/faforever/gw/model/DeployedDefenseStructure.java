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
@Table(name = "gw_deployed_defense_structure")
public class DeployedDefenseStructure {
	private UUID id;
	private DefenseStructure structure;
	private Planet planet;
	private Faction faction;
	private CreditJournalEntry creditJournalEntry;

	public DeployedDefenseStructure(DefenseStructure structure, Planet planet, Faction faction, CreditJournalEntry creditJournalEntry) {
		this.structure = structure;
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
	@JoinColumn(name = "fk_structure")
	public DefenseStructure getStructure() {
		return structure;
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

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "deployedDefenseStructure")
	public CreditJournalEntry getCreditJournalEntry() {
		return creditJournalEntry;
	}
}
