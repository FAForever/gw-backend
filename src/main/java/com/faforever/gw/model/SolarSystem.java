package com.faforever.gw.model;

import com.yahoo.elide.annotation.Include;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
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
    private List<Planet> planets;
    private List<QuantumGateLink> incomingLinks = new ArrayList<>();
    private List<QuantumGateLink> outgoingLinks = new ArrayList<>();

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
    public List<Planet> getPlanets() {
        return planets;
    }

    @Column(name = "name", unique = true)
    public String getName() {
        return name;
    }

    @OneToMany(mappedBy = "origin")
    public List<QuantumGateLink> getIncomingLinks() {
        return incomingLinks;
    }

    @OneToMany(mappedBy = "destination")
    public List<QuantumGateLink> getOutgoingLinks() {
        return outgoingLinks;
    }
}
