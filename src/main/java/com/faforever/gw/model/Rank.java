package com.faforever.gw.model;

import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="gw_rank")
@Setter
@NoArgsConstructor
public class Rank implements Serializable{
    Long xpMin;
    private Integer level;
    private List<GwCharacter> characters;
    private String uefTitle;
    private String aeonTitle;
    private String cybranTitle;
    private String seraphimTitle;

    @Id
    public Integer getLevel() {
        return level;
    }

    @Column(name = "xp_min", nullable = false, updatable = false, length = 20)
    public Long getXpMin() {
        return xpMin;
    }

    @OneToMany(mappedBy = "rank")
    public List<GwCharacter> getCharacters() {
        return characters;
    }

    @Column(name = "uef_title", nullable = false, updatable = false, length = 20)
    public String getUefTitle() {
        return uefTitle;
    }

    @Column(name = "aeon_title", nullable = false, updatable = false, length = 20)
    public String getAeonTitle() {
        return aeonTitle;
    }

    @Column(name = "cybran_title", nullable = false, updatable = false, length = 20)
    public String getCybranTitle() {
        return cybranTitle;
    }

    @Column(name = "seraphim_title", nullable = false, updatable = false, length = 20)
    public String getSeraphimTitle() {
        return seraphimTitle;
    }

    @Transient
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
