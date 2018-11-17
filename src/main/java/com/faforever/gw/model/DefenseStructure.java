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
	private double prize;

	public DefenseStructure(Unit unit, double prize) {
		this.unit = unit;
		this.prize = prize;
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

	@Column(name = "prize")
	public double getPrize() {
		return prize;
	}
}
