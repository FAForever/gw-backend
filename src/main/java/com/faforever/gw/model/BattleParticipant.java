package com.faforever.gw.model;

import com.yahoo.elide.annotation.Include;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name="gw_battle_participant")
@Setter
@Include
@NoArgsConstructor
public class BattleParticipant implements Serializable{
    private UUID id;
    private Battle battle;
    private GwCharacter character;
    private BattleRole role;
    private BattleParticipantResult result;

    public BattleParticipant(Battle battle, GwCharacter gwCharacter, BattleRole role) {
        this.battle = battle;
        this.character = gwCharacter;
        this.role = role;
    }

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    public UUID getId() {
        return id;
    }

    @ManyToOne
    @JoinColumn(name = "fk_battle")
    public Battle getBattle() {
        return battle;
    }

    @ManyToOne
    @JoinColumn(name = "fk_character")
    public GwCharacter getCharacter() {
        return character;
    }

    @Column(name = "role", length = 1)
    public BattleRole getRole() {
        return role;
    }

    @Column(name = "result", length = 1)
    public BattleParticipantResult getResult() {
        return result;
    }

    @Transient
    public Faction getFaction() {
        return character.getFaction();
    }
}
