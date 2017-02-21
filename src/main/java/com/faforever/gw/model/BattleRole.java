package com.faforever.gw.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public enum BattleRole {
    ATTACKER("A", "attacker"),
    DEFENDER("D", "defender");

    @Converter(autoApply = true)
    public static class BattleRoleConverter implements AttributeConverter<BattleRole, String> {

        @Override
        public String convertToDatabaseColumn(BattleRole attribute) {
            return attribute.getDbKey();
        }

        @Override
        public BattleRole convertToEntityAttribute(String dbData) {
            return BattleRole.fromDbKeyString(dbData);
        }
    }


    private static final Map<String, BattleRole> fromDbKey;
    private static final Map<String, BattleRole> fromName;

    static {
        fromDbKey = new HashMap<>();
        fromName= new HashMap<>();
        for (com.faforever.gw.model.BattleRole BattleRole : values()) {
            fromDbKey.put(BattleRole.dbKey, BattleRole);
            fromName.put(BattleRole.name, BattleRole);
        }
    }

    @Getter
    private final String dbKey;

    @Getter
    private final String name;


    public static BattleRole fromDbKeyString(String string) {
        return fromDbKey.get(string);
    }

    public static BattleRole fromNameString(String string) {
        return fromName.get(string);
    }
}