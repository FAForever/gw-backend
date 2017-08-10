package com.faforever.gw.model;

import com.yahoo.elide.annotation.Include;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Setter
@Include
@Table(name = "gw_quantum_gate_link",
        uniqueConstraints = @UniqueConstraint(columnNames = {"fk_ss_origin", "fk_ss_destination"}))
public class QuantumGateLink {
    private UUID id;
    private SolarSystem origin;
    private SolarSystem destination;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    public UUID getId() {
        return id;
    }

    @ManyToOne
    @JoinColumn(name = "fk_ss_origin")
    public SolarSystem getOrigin() {
        return origin;
    }

    @ManyToOne
    @JoinColumn(name = "fk_ss_destination")
    public SolarSystem getDestination() {
        return destination;
    }
}
