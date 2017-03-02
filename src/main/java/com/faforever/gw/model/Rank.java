package com.faforever.gw.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="gw_rank")
@Getter
@Setter
@NoArgsConstructor
public class Rank implements Serializable{
    @Id
    private Integer level;

    @Column(name = "xp_min", nullable = false, updatable = false, length = 20)
    Long xpMin;

    @OneToMany(mappedBy = "rank")
    private List<GwCharacter> characters;

    @Column(name = "uef_title", nullable = false, updatable = false, length = 20)
    private String uefTitle;

    @Column(name = "aeon_title", nullable = false, updatable = false, length = 20)
    private String aeonTitle;

    @Column(name = "cybran_title", nullable = false, updatable = false, length = 20)
    private String cybranTitle;

    @Column(name = "seraphim_title", nullable = false, updatable = false, length = 20)
    private String seraphimTitle;

    public String getTitle(Faction faction) {
        switch (faction) {
            case UEF:
                return uefTitle;
            case AEON:
                return aeonTitle;
            case CYBRAN:
                return cybranTitle;
            case SERAPHIM:
                return seraphimTitle;
        }

        throw new RuntimeException("This code is not allowed to be reached");
    }

}
