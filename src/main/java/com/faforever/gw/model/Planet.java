package com.faforever.gw.model;

import com.yahoo.elide.annotation.Include;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Include(rootLevel = true)
@Table(name = "gw_planet")
public class Planet implements Serializable {

    private UUID id;
    private List<Battle> battles;
    private int orbitLevel;
    private int size;
    private boolean habitable;
    private Ground ground;
    private Map map;
    private Faction currentOwner;

    @Id
//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    public UUID getId() {
        return id;
    }

    // TODO: sun system

    @OneToMany(mappedBy = "planet")
    public List<Battle> getBattles() {
        return battles;
    }

    @Column(name = "orbit_level")
    public int getOrbitLevel() {
        return orbitLevel;
    }

    @Column(name = "size")
    public int getSize() {
        return size;
    }

    @Column(name = "habitable")
    public boolean isHabitable() {
        return habitable;
    }

    @Column(name = "ground", length = 1)
    public Ground getGround() {
        return ground;
    }

    @ManyToOne
    @JoinColumn(name = "fk_map")
    public Map getMap() {
        return map;
    }

    /***
     * The current owner is the winner of the last finished battle over this planet.
     * In case of a draw, the defending faction (= previous owner) remains the owner.
     */
    @Formula("(select coalesce(b.winning_faction, b.defending_faction) " +
            "from gw_battle b where b.fk_planet = id and b.status='F' and " +
            "b.ended_at = (select max(b2.ended_at) from gw_battle b2 where b2.fk_planet = id and b2.status='F'))")
    public Faction getCurrentOwner() {
        return currentOwner;
    }

    public void setCurrentOwner(Faction value) {
        currentOwner = value;
    }
}
