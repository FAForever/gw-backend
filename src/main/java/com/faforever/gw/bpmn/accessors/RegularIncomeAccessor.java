package com.faforever.gw.bpmn.accessors;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class RegularIncomeAccessor {
    private final Map<String, Object> processVariables;

    public Collection<UUID> getActiveCharacters()  { return (Collection<UUID>)processVariables.getOrDefault("activeCharacters", Collections.EMPTY_LIST); }

    /***
     * !! only to be used in subprocess !!
     * Returns a single character of the current subprocess
     * @return UUID of GwCharacter
     */
    public UUID getCharacter() { return (UUID)processVariables.get("character");}

    private RegularIncomeAccessor(Map<String, Object> processVariables) {
        this.processVariables = processVariables;
    }

    public static RegularIncomeAccessor of(Map<String, Object> processVariables) { return new RegularIncomeAccessor(processVariables); }
}
