package com.faforever.gw.model;

import com.yahoo.elide.annotation.Include;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Entity
@Include(rootLevel = true)
@Table(name = "gw_reinforcements_transaction")
public class ReinforcementsTransaction {
	private UUID id;
	private Character character;
	@Nullable private Battle battle;
	private CreditJournalEntry creditJournalEntry;
	private Timestamp createdAt;
	private ReinforcementsGroup group;
	private int quantity;

	public ReinforcementsTransaction(Character character, Battle battle, CreditJournalEntry creditJournalEntry, ReinforcementsGroup group, int quantity) {
		this.character = character;
		this.battle = battle;
		this.creditJournalEntry = creditJournalEntry;
		this.group = group;
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

	@ManyToMany
	@JoinTable(name = "gw_reinforcement_transaction_group",
			joinColumns = @JoinColumn(name = "fk_reinforcement_transaction"),
			inverseJoinColumns = @JoinColumn(name = "fk_reinforcement_group"))
	public ReinforcementsGroup getGroup() {
		return group;
	}

	@Column(name = "quantity")
	public int getQuantity() {
		return quantity;
	}
}
