package com.faforever.gw.model;

import com.yahoo.elide.annotation.Include;
import lombok.Setter;

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
    private SolarSystem solarSystem;

    @Id
//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    public UUID getId() {
        return id;
    }

    @ManyToOne
    @JoinColumn(name = "fk_solar_system")
    public SolarSystem getSolarSystem() {
        return solarSystem;
    }

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

    @Column(name = "current_owner")
    public Faction getCurrentOwner() {
        return currentOwner;
    }
}
