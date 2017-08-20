package com.faforever.gw.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yahoo.elide.annotation.Include;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Setter
@Include(rootLevel = true)
@Table(name = "gw_solar_system",
        uniqueConstraints = @UniqueConstraint(columnNames = {"x", "y", "z"}))
public class SolarSystem {
    private UUID id;
    private long x;
    private long y;
    private long z;
    private String name;
    private Set<Planet> planets = new HashSet<>();
    private Set<SolarSystem> connectedSystems = new HashSet<>();

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    public UUID getId() {
        return id;
    }

    @Column(name = "x")
    public long getX() {
        return x;
    }

    @Column(name = "y")
    public long getY() {
        return y;
    }

    @Column(name = "z")
    public long getZ() {
        return z;
    }

    public static double getDistanceBetween(SolarSystem pointA, SolarSystem pointB) {
        double x = pointA.getX() - pointB.getX();
        double y = pointA.getY() - pointB.getY();
        double z = pointA.getZ() - pointB.getZ();
        return Math.sqrt(x * x + y * y + z * z);
    }

    @OneToMany(mappedBy = "solarSystem")
    public Set<Planet> getPlanets() {
        return planets;
    }

    @Column(name = "name", unique = true)
    public String getName() {
        return name;
    }

    @ManyToMany
    @JoinTable(name = "gw_quantum_links",
            joinColumns = @JoinColumn(name = "fk_ss_from"),
            inverseJoinColumns = @JoinColumn(name = "fk_ss_to"))
    public Set<SolarSystem> getConnectedSystems() {
        return connectedSystems;
    }

    @JsonIgnore
    public Faction uniqueOwner() {
        Faction uniqueFaction = null;

        boolean first = true;

        for (Planet p : planets) {
            if (first) {
                first = false;
                uniqueFaction = p.getCurrentOwner();
            } else {
                if (uniqueFaction != p.getCurrentOwner())
                    return null;
            }
        }

        return uniqueFaction;
    }

    /*
     * SolarSystem is reachable by a given Faction if it is connected to a SolarSystem uniquely owned by that Faction or a Planet within this SolarSystem is owned by that Faction.
     * ! This function only works for bidirectional SolarSystem-linking !
     */
    @JsonIgnore
    public boolean isReachable(Faction faction) {

        for (SolarSystem system : getConnectedSystems()) {
            if(system.uniqueOwner() == faction) {
                return true;
            }
        }

        for (Planet planet : planets) {
            if(planet.getCurrentOwner() == faction) {
                return true;
            }
        }

        return false;

    }
}
