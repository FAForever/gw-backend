package com.faforever.gw.model;

import com.yahoo.elide.annotation.Include;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name="gw_credit_journal")
@Setter
@Include
@NoArgsConstructor
public class CreditJournalEntry implements Serializable {
	private UUID id;
	private GwCharacter character;
	private Battle battle;
	private ReinforcementsTransaction reinforcementsTransaction;
	private CreditJournalEntryReason reason;
	private double amount;
	private Timestamp createdAt;

	public CreditJournalEntry(GwCharacter character, @Nullable Battle battle, CreditJournalEntryReason reason, double amount) {
		this.character = character;
		this.battle = battle;
		this.reason = reason;
		this.amount = amount;
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

	@Nullable
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fk_reinforcements_transaction")
	public ReinforcementsTransaction getReinforcementsTransaction() {
		return reinforcementsTransaction;
	}

	@Column(name = "reason", length = 1)
	public CreditJournalEntryReason getReason() {
		return reason;
	}

	@Column(name = "amount")
	public double getAmount() {
		return amount;
	}

	@Column(name = "created_at")
	@CreationTimestamp
	public Timestamp getCreatedAt() {
		return createdAt;
	}
}
