package com.faforever.gw.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "gw_character")
public class GwCharacter implements Serializable {
    @Id
//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @Column(name = "faf_id", nullable = false, updatable = false)
    private int fafId;

    @Column(name = "name", nullable = false, updatable = false, length = 30)
    private String name;

    @Column(name = "faction", nullable = false, updatable = false, length = 1)
    private Faction faction;

    @Column(name = "xp", nullable = false, updatable = false)
    private Long xp;

    @OneToMany(mappedBy = "character")
    private List<BattleParticipant> battleParticipantList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "fk_killer")
    private GwCharacter killer;

    @OneToMany(mappedBy = "killer")
    private Set<GwCharacter> killedBy;

    @ManyToOne
    @JoinColumn(name = "fk_rank")
    private Rank rank;

    public String getTitle() {
        return rank.getTitle(getFaction());
    }
}
