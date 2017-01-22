package com.faforever.gw.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public enum Faction {
    AEON("A", "aeon"),
    CYBRAN("C", "cybran"),
    UEF("U", "uef"),
    SERAPHIM("S", "seraphim");


    @Converter(autoApply = true)
    public static class FactionConverter implements AttributeConverter<Faction, String> {

        @Override
        public String convertToDatabaseColumn(Faction attribute) {
            return attribute.getDbKey();
        }

        @Override
        public Faction convertToEntityAttribute(String dbData) {
            return Faction.fromString(dbData);
        }
    }


    private static final Map<String, Faction> fromDbKey;
    private static final Map<String, Faction> fromName;

    static {
        fromDbKey = new HashMap<>();
        fromName= new HashMap<>();
        for (Faction faction : values()) {
            fromDbKey.put(faction.dbKey, faction);
            fromName.put(faction.name, faction);
        }
    }

    @Getter
    private final String dbKey;

    @Getter
    private final String name;


    public static Faction fromString(String string) {
        return fromDbKey.get(string);
    }

}