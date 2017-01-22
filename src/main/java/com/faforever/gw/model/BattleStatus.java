package com.faforever.gw.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public enum BattleStatus implements Serializable{
    INITIATED("I", "initiated"),
    CANCELED("C", "canceled"),
    RUNNING("R", "running"),
    FINISHED("F", "finished");


    @Converter(autoApply = true)
    public static class BattleStatusConverter implements AttributeConverter<BattleStatus, String> {

        @Override
        public String convertToDatabaseColumn(BattleStatus attribute) {
            return attribute.getDbKey();
        }

        @Override
        public BattleStatus convertToEntityAttribute(String dbData) {
            return BattleStatus.fromString(dbData);
        }
    }


    private static final Map<String, BattleStatus> fromDbKey;
    private static final Map<String, BattleStatus> fromName;

    static {
        fromDbKey = new HashMap<>();
        fromName= new HashMap<>();
        for (BattleStatus battleStatus : values()) {
            fromDbKey.put(battleStatus.dbKey, battleStatus);
            fromName.put(battleStatus.name, battleStatus);
        }
    }

    @Getter
    private final String dbKey;

    @Getter
    private final String name;


    public static BattleStatus fromString(String string) {
        return fromDbKey.get(string);
    }
}
