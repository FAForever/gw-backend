package com.faforever.gw.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name="gw_planet")
public class Planet implements Serializable {
    @Id
//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    // TODO: sun system
    // TODO: map

    @OneToMany(mappedBy = "planet")
    private List<Battle> battles;

    @Column(name="orbit_level")
    private int orbitLevel;

    @Column(name="size")
    private int size;

    @Column(name="habitable")
    private boolean habitable;

    @Column(name="ground", length = 1)
    private Ground ground;

    /***
     * The current owner is the winner of the last finished battle over this planet.
     * In case of a draw, the defending faction (= previous owner) remains the owner.
     */
    @Formula("(select coalesce(b.winning_faction, b.defending_faction) " +
             "from gw_battle b where b.fk_planet = id and b.status='F' and " +
             "b.ended_at = (select max(b2.ended_at) from gw_battle b2 where b2.fk_planet = id and b2.status='F'))")
    @Setter(AccessLevel.NONE)
    private Faction currentOwner;
}
