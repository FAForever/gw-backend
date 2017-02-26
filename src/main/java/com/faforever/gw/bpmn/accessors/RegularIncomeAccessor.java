package com.faforever.gw.bpmn.accessors;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class RegularIncomeAccessor {
    private final Map<String, Object> processVariables;

    public Collection<UUID> getActiveCharacters()  { return (Collection<UUID>)processVariables.getOrDefault("activeCharacters", Collections.EMPTY_LIST); }

    public UUID getCharacter_Local() { return (UUID)processVariables.get("character");}

    public Long getCreditsDelta_Local() { return (Long)processVariables.get("creditsDelta");}

    public Long getCreditsTotal_Local() { return (Long)processVariables.get("creditsTotal");}

    private RegularIncomeAccessor(Map<String, Object> processVariables) {
        this.processVariables = processVariables;
    }

    public static RegularIncomeAccessor of(Map<String, Object> processVariables) { return new RegularIncomeAccessor(processVariables); }
}
