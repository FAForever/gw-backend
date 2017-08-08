package com.faforever.gw.model;

import com.yahoo.elide.annotation.Include;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Include(rootLevel = true)
@Table(name = "gw_solar_system")
public class SolarSystem {
    private UUID id;
    private long x;
    private long y;
    private long z;
    private String name;
    private List<Planet> planets;

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

    @Column(name = "name")
    public String getName() {
        return name;
    }

    @OneToMany(mappedBy = "solarSystem")
    public List<Planet> getPlanets() {
        return planets;
    }
}
