package com.faforever.gw.model;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Entity
@Table(name = "gw_reinforcements_transaction")
public class ReinforcementsTransaction implements Serializable {
	private UUID id;
	private GwCharacter character;
	@Nullable private Battle battle;
	@Nullable private CreditJournalEntry creditJournalEntry; // May be null if the reinforcements have been used in a battle
	private Timestamp createdAt;
	private Reinforcement reinforcement;
	private int quantity; // Negative quantity indicates spending in battle

	public ReinforcementsTransaction(GwCharacter character, Battle battle, CreditJournalEntry creditJournalEntry, Reinforcement reinforcement, int quantity) {
		this.character = character;
		this.battle = battle;
		this.creditJournalEntry = creditJournalEntry;
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
	@JoinColumn(name = "fk_character")
	public GwCharacter getCharacter() {
		return character;
	}

	@Nullable
	@ManyToOne
	@JoinColumn(name = "fk_battle")
	public Battle getBattle() {
		return battle;
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fk_credit_journal_entry")
	public CreditJournalEntry getCreditJournalEntry() {
		return creditJournalEntry;
	}

	@Column(name = "created_at")
	@CreationTimestamp
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
