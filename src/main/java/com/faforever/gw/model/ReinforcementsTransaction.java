package com.faforever.gw.model;

import com.yahoo.elide.annotation.Include;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Entity
@Include(rootLevel = true)
@Table(name = "gw_reinforcements_transaction")
public class ReinforcementsTransaction implements Serializable {
	private UUID id;
	private Character character;
	@Nullable private Battle battle;
	@Nullable private CreditJournalEntry creditJournalEntry; // May be null if the reinforcements have been used in a battle
	private Timestamp createdAt;
	private Reinforcement reinforcement;
	private int quantity; // Negative quantity indicates spending in battle

	public ReinforcementsTransaction(Character character, Battle battle, CreditJournalEntry creditJournalEntry, Reinforcement reinforcement, int quantity) {
		this.character = character;
		this.battle = battle;
		this.creditJournalEntry = creditJournalEntry;
		this.reinforcement = reinforcement;
		this.quantity = quantity;

		this.createdAt = Timestamp.from(Instant.now());
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

	@Nullable
	@ManyToOne
	@JoinColumn(name = "fk_battle")
	public Battle getBattle() {
		return battle;
	}

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "creditJournalEntry")
	@JoinColumn(name = "fk_credit_journal_entry")
	public CreditJournalEntry getCreditJournalEntry() {
		return creditJournalEntry;
	}

	@Column(name = "created_at")
	public Timestamp getCreatedAt() {
		return createdAt;
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
