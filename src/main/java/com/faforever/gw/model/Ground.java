package com.faforever.gw.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public enum Ground {
    WATER("W", "water"),
    SOIL("S", "soil"),
    LAVA("L", "lava"),
    DESERT("D", "desert"),
    FROST("F", "frost");


    @Converter(autoApply = true)
    public static class GroundConverter implements AttributeConverter<Ground, String> {

        @Override
        public String convertToDatabaseColumn(Ground attribute) {
            return attribute.getDbKey();
        }

        @Override
        public Ground convertToEntityAttribute(String dbData) {
            return Ground.fromString(dbData);
        }
    }


    private static final Map<String, Ground> fromDbKey;
    private static final Map<String, Ground> fromName;

    static {
        fromDbKey = new HashMap<>();
        fromName= new HashMap<>();
        for (com.faforever.gw.model.Ground Ground : values()) {
            fromDbKey.put(Ground.dbKey, Ground);
            fromName.put(Ground.name, Ground);
        }
    }

    @Getter
    private final String dbKey;

    @Getter
    private final String name;


    public static Ground fromString(String string) {
        return fromDbKey.get(string);
    }

}
