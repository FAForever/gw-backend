package com.faforever.gw.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public enum BattleParticipantResult {
    @JsonProperty("victory")
    VICTORY("V", "victory"),
    @JsonProperty("death")
    DEATH("D", "death"),
    @JsonProperty("recall")
    RECALL("R", "recall");


    @Converter(autoApply = true)
    public static class BattleParticipantResultConverter implements AttributeConverter<BattleParticipantResult, String> {

        @Override
        public String convertToDatabaseColumn(BattleParticipantResult attribute) {
            if(attribute==null)
                return null;
            return attribute.getDbKey();
        }

        @Override
        public BattleParticipantResult convertToEntityAttribute(String dbData) {
            if(dbData==null)
                return null;
            return BattleParticipantResult.fromString(dbData);
        }
    }


    private static final Map<String, BattleParticipantResult> fromDbKey;
    private static final Map<String, BattleParticipantResult> fromName;

    static {
        fromDbKey = new HashMap<>();
        fromName= new HashMap<>();
        for (com.faforever.gw.model.BattleParticipantResult BattleParticipantResult : values()) {
            fromDbKey.put(BattleParticipantResult.dbKey, BattleParticipantResult);
            fromName.put(BattleParticipantResult.name, BattleParticipantResult);
        }
    }

    @Getter
    private final String dbKey;

    @Getter
    private final String name;


    public static BattleParticipantResult fromString(String string) {
        return fromDbKey.get(string);
    }

}