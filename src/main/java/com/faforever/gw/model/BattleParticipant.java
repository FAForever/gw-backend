package com.faforever.gw.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name="gw_battle_participant")
@Getter
@Setter
@NoArgsConstructor
public class BattleParticipant implements Serializable{
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @ManyToOne
    @JoinColumn(name="fk_battle")
    private Battle battle;

    @ManyToOne
    @JoinColumn(name = "fk_character")
    private GwCharacter character;

    @Column(name = "role", length = 1)
    private BattleRole role;

    @Column(name="result", length = 1)
    private BattleParticipantResult result;

    public BattleParticipant(Battle battle, GwCharacter gwCharacter, BattleRole role) {
        this.battle = battle;
        this.character = gwCharacter;
        this.role = role;
    }

    public Faction getFaction() {
        return character.getFaction();
    }
}
