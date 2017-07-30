package com.faforever.gw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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


    private static final Map<String, Faction> fromDbKey;
    private static final Map<String, Faction> fromName;

    static {
        fromDbKey = new HashMap<>();
        fromName = new HashMap<>();
        for (Faction faction : values()) {
            fromDbKey.put(faction.dbKey, faction);
            fromName.put(faction.name, faction);
        }
    }

    @Getter
    private final String dbKey;
    private final String name;

    public static Faction fromDbString(String string) {
        return fromDbKey.get(string);
    }

    @JsonCreator
    public static Faction fromName(String name) {
        return fromName.get(name);
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @Converter(autoApply = true)
    public static class FactionConverter implements AttributeConverter<Faction, String> {

        @Override
        public String convertToDatabaseColumn(Faction attribute) {
            if (attribute == null)
                return null;
            return attribute.getDbKey();
        }

        @Override
        public Faction convertToEntityAttribute(String dbData) {
            if (dbData == null)
                return null;
            return Faction.fromDbString(dbData);
        }
    }

}