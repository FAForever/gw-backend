package com.faforever.gw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@AllArgsConstructor
public enum TechLevel {
    T1("1", "T1"),
    T2("2", "T2"),
    T3("3", "T3"),
    T4("4", "T4");


    private static final Map<String, TechLevel> fromDbKey;
    private static final Map<String, TechLevel> fromName;
    private static final Random random = new Random();

    static {
        fromDbKey = new HashMap<>();
        fromName = new HashMap<>();
        for (TechLevel faction : values()) {
            fromDbKey.put(faction.dbKey, faction);
            fromName.put(faction.name, faction);
        }
    }

    @Getter
    private final String dbKey;
    private final String name;

    public static TechLevel fromDbString(String string) {
        return fromDbKey.get(string);
    }

    @JsonCreator
    public static TechLevel fromName(String name) {
        return fromName.get(name);
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @Converter(autoApply = true)
    public static class TechLevelConverter implements AttributeConverter<TechLevel, String> {

        @Override
        public String convertToDatabaseColumn(TechLevel attribute) {
            if (attribute == null)
                return null;
            return attribute.getDbKey();
        }

        @Override
        public TechLevel convertToEntityAttribute(String dbData) {
            if (dbData == null)
                return null;
            return TechLevel.fromDbString(dbData);
        }
    }
}