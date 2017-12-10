package com.faforever.gw.model;

import com.yahoo.elide.annotation.Include;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Setter
@Table(name = "gw_battle")
@Include(rootLevel = true)
@NoArgsConstructor
public class Battle implements Serializable {

    private UUID id;
    private long fafGameId;
    private Planet planet;
    private List<BattleParticipant> participants = new ArrayList<>();
    private BattleStatus status;
    private Timestamp initiatedAt;
    private double waitingProgress = 0.0;
    private Timestamp startedAt;
    private Timestamp endedAt;
    private Faction attackingFaction;
    private Faction defendingFaction;
    private Faction winningFaction;
    public Battle(UUID id, Planet planet, Faction attackingFaction, Faction defendingFaction) {
        this.id = id;
        this.planet = planet;
        this.attackingFaction = attackingFaction;
        this.defendingFaction = defendingFaction;
        this.status = BattleStatus.INITIATED;
        this.initiatedAt = Timestamp.from(Instant.now());
    }

    @Id
    public UUID getId() {
        return id;
    }

    @Column(name = "fafGameId")
    public long getFafGameId() {
        return fafGameId;
    }

    @ManyToOne
    @JoinColumn(name = "fk_planet")
    public Planet getPlanet() {
        return planet;
    }

    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @NotNull
    public List<BattleParticipant> getParticipants() {
        return participants;
    }

    @Column(name = "status", nullable = false, length = 1)
    public BattleStatus getStatus() {
        return status;
    }

    @Column(name = "initiated_at", nullable = false)
    public Timestamp getInitiatedAt() {
        return initiatedAt;
    }

    @Column(name = "waitingProgress", nullable = false)
    public double getWaitingProgress() {
        return waitingProgress;
    }

    @Column(name = "started_at")
    public Timestamp getStartedAt() {
        return startedAt;
    }

    @Column(name = "ended_at")
    public Timestamp getEndedAt() {
        return endedAt;
    }

    @Column(name = "attacking_faction", nullable = true, updatable = false, length = 1)
    public Faction getAttackingFaction() {
        return attackingFaction;
    }

    @Column(name = "defending_faction", nullable = true, updatable = false, length = 1)
    public Faction getDefendingFaction() {
        return defendingFaction;
    }

    @Column(name = "winning_faction", length = 1)
    public Faction getWinningFaction() {
        return winningFaction;
    }

    public Optional<BattleParticipant> getParticipant(GwCharacter character) {
        return getParticipants().stream()
                .filter(battleParticipant -> battleParticipant.getCharacter().getId().equals(character.getId()))
                .findFirst();
    }
}
