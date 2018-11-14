package com.faforever.gw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public enum ReinforcementsType implements Serializable {
	INITIAL_UNIT_WARP("W", "initialUnitWarp"),
	PERIODIC_UNIT_WARP("P", "periodicUnitWarp"),
	TRANSPORTED_UNITS("T", "transportedUnits"),
	PASSIVE_ITEMS("I", "passiveItems");


	private static final java.util.Map<String, ReinforcementsType> fromDbKey;
	private static final Map<String, ReinforcementsType> fromName;

	static {
		fromDbKey = new HashMap<>();
		fromName = new HashMap<>();
		for (ReinforcementsType groupType : values()) {
			fromDbKey.put(groupType.dbKey, groupType);
			fromName.put(groupType.name, groupType);
		}
	}

	@Getter
	private final String dbKey;
	private final String name;

	public static ReinforcementsType fromDbString(String string) {
		return fromDbKey.get(string);
	}

	@JsonCreator
	public static ReinforcementsType fromName(String name) {
		return fromName.get(name);
	}

	@JsonValue
	public String getName() {
		return name;
	}

	@Converter(autoApply = true)
	public static class ReinforcementsGroupTypeConverter implements AttributeConverter<ReinforcementsType, String> {

		@Override
		public String convertToDatabaseColumn(ReinforcementsType attribute) {
			if (attribute == null)
				return null;
			return attribute.getDbKey();
		}

		@Override
		public ReinforcementsType convertToEntityAttribute(String dbData) {
			if (dbData == null)
				return null;
			return ReinforcementsType.fromDbString(dbData);
		}
	}
}
