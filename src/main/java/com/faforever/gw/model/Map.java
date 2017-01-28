package com.faforever.gw.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "gw_map")
@NoArgsConstructor
public class Map implements Serializable{
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @Column(name = "faf_map_id", nullable = false, updatable = false)
    private Integer fafMapId;

    @Column(name = "faf_map_version", nullable = false, updatable = false)
    private Integer fafMapVersion;

    @Column(name = "total_slots", nullable = false, updatable = false)
    private Integer totalSlots;

    @Column(name = "size", nullable = false, updatable = false)
    private Integer size;

    @Column(name = "ground", nullable = false, updatable = false, length = 1)
    private Ground ground;

    @OneToMany(mappedBy = "map")
    private List<Planet> planetList = new ArrayList<>();
}
