package com.faforever.gw.bpmn.accessors;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * Accessor class for BPMN process "calculate promotions"
 */
public class CalculatePromotionsAccessor extends BaseAccessor {
    private CalculatePromotionsAccessor(DelegateExecution processContext) {
        super(processContext);
    }

    public Collection<UUID> getActiveCharacters() {
        return (Collection<UUID>) getOrDefault("activeCharacters", Collections.EMPTY_LIST);
    }

    public UUID getCharacter_Local() {
        return (UUID) get("character");
    }

    public Boolean isRankAvailable_Local() {
        return (Boolean)get("rankAvailable");
    }

    public CalculatePromotionsAccessor setRankAvailable(boolean rankAvailable)
    {
        set("rankAvailable", rankAvailable);
        return this;
    }

    public Integer getNewRank_Local() {
        return (Integer) get("newRank");
    }

    public CalculatePromotionsAccessor setNewRank(Integer newRank) {
        set("newRank", newRank);
        return this;
    }

    public static CalculatePromotionsAccessor of(DelegateExecution processContext) {
        return new CalculatePromotionsAccessor(processContext);
    }
}
