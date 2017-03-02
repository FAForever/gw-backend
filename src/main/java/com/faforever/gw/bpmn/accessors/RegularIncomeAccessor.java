package com.faforever.gw.bpmn.accessors;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class RegularIncomeAccessor extends BaseAccessor {

    public Collection<UUID> getActiveCharacters() {
        return (Collection<UUID>) getOrDefault("activeCharacters", Collections.EMPTY_LIST);
    }

    public UUID getCharacter_Local() {
        return (UUID) get("character");
    }

    public RegularIncomeAccessor setCharacter(UUID character) {
        set("character", character);
        return this;
    }

    public Long getCreditsDelta_Local() {
        return (Long) get("creditsDelta");
    }

    public RegularIncomeAccessor setCreditsDelta(Long creditsDelta) {
        set("creditsDelta", creditsDelta);
        return this;
    }

    public Long getCreditsTotal_Local() {
        return (Long) get("creditsTotal");
    }

    public RegularIncomeAccessor setCreditsTotal(Long creditsTotal) {
        set("creditsTotal", creditsTotal);
        return this;
    }

    private RegularIncomeAccessor(DelegateExecution processContext) {
        super(processContext);
    }

    public static RegularIncomeAccessor of(DelegateExecution processContext) {
        return new RegularIncomeAccessor(processContext);
    }
}
