package com.faforever.gw.model;

import com.yahoo.elide.annotation.Include;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.*;

@NoArgsConstructor
@Setter
@Entity
@Include(rootLevel = true)
@Table(name = "gw_character")
public class GwCharacter implements Serializable {
    private UUID id;
    private long fafId;
    private String name;
    private Faction faction;
    private Long xp;
    private List<BattleParticipant> battleParticipantList = new ArrayList<>();
    private GwCharacter killer;
    private Set<GwCharacter> killedBy;
    private Rank rank;
    private List<CreditJournalEntry> creditJournalList = new ArrayList<>();
    private List<ReinforcementsTransaction> reinforcementsTransactionList = new ArrayList<>();
    private List<ReinforcementsGroup> reinforcementsGroupList = new ArrayList<>();

    @Id
//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    public UUID getId() {
        return id;
    }

    @Column(name = "faf_id", nullable = false, updatable = false)
    public long getFafId() {
        return fafId;
    }

    @Column(name = "name", nullable = false, updatable = false, length = 30)
    public String getName() {
        return name;
    }

    @Column(name = "faction", nullable = false, updatable = false, length = 1)
    public Faction getFaction() {
        return faction;
    }

    @Column(name = "xp", nullable = false, updatable = false)
    public Long getXp() {
        return xp;
    }

    @OneToMany(mappedBy = "character")
    public List<BattleParticipant> getBattleParticipantList() {
        return battleParticipantList;
    }

    @ManyToOne
    @JoinColumn(name = "fk_killer")
    public GwCharacter getKiller() {
        return killer;
    }

    @OneToMany(mappedBy = "killer")
    public Set<GwCharacter> getKilledBy() {
        return killedBy;
    }

    @ManyToOne
    @JoinColumn(name = "fk_rank")
    public Rank getRank() {
        return rank;
    }

    @OneToMany(mappedBy = "character")
    public List<CreditJournalEntry> getCreditJournalList() {
        return creditJournalList;
    }

    @OneToMany(mappedBy = "character")
    public List<ReinforcementsTransaction> getReinforcementsTransactionList() {
        return reinforcementsTransactionList;
    }

    @OneToMany(mappedBy = "character")
    public List<ReinforcementsGroup> getReinforcementsGroupList() {
        return reinforcementsGroupList;
    }

    @Transient
    public String getTitle() {
        return rank.getTitle(getFaction());
    }

    @Transient
    @Transactional
    public Optional<Battle> getCurrentBattle() {
        Hibernate.initialize(this);
        Hibernate.initialize(getBattleParticipantList());
        val currentParticipant = getBattleParticipantList().stream()
                .filter(battleParticipant -> {
                    BattleStatus battleStatus = battleParticipant.getBattle().getStatus();
                    return battleStatus == BattleStatus.INITIATED || battleStatus == BattleStatus.RUNNING;
                })
                .findFirst();

        return currentParticipant.map(BattleParticipant::getBattle);
    }
}
