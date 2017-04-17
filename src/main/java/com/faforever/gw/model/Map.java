package com.faforever.gw.model;

import com.yahoo.elide.annotation.Include;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Table(name = "gw_map")
@Include(rootLevel = true)
@NoArgsConstructor
public class Map implements Serializable{

    private UUID id;
    private Integer fafMapId;
    private Integer fafMapVersion;
    private Integer totalSlots;
    private Integer size;
    private Ground ground;
    private List<Planet> planetList = new ArrayList<>();

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    public UUID getId() {
        return id;
    }

    @Column(name = "faf_map_id", nullable = false, updatable = false)
    public Integer getFafMapId() {
        return fafMapId;
    }

    @Column(name = "faf_map_version", nullable = false, updatable = false)
    public Integer getFafMapVersion() {
        return fafMapVersion;
    }

    @Column(name = "total_slots", nullable = false, updatable = false)
    public Integer getTotalSlots() {
        return totalSlots;
    }

    @Column(name = "size", nullable = false, updatable = false)
    public Integer getSize() {
        return size;
    }

    @Column(name = "ground", nullable = false, updatable = false, length = 1)
    public Ground getGround() {
        return ground;
    }

    @OneToMany(mappedBy = "map")
    public List<Planet> getPlanetList() {
        return planetList;
    }
}
