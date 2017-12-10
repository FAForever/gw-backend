package com.faforever.gw.bpmn.accessors;

import com.faforever.gw.model.BattleRole;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.GameResult;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import java.util.UUID;

/**
 * Accessor class for BPMN process "planetary assault"
 */
public class PlanetaryAssaultAccessor extends UserInteractionProcessAccessor {
    private PlanetaryAssaultAccessor(DelegateExecution processContext) {
        super(processContext);
    }

    public static PlanetaryAssaultAccessor of(DelegateExecution processContext) {
        return new PlanetaryAssaultAccessor(processContext);
    }

    public UUID getBattleId() {
        return getUUID("battle");
    }

    public PlanetaryAssaultAccessor setBattleId(UUID battleId) {
        set("battle", battleId);
        return this;
    }

    public UUID getPlanetId() {
        return getUUID("planet");
    }

    public Faction getAttackingFaction() {
        return getFaction("attackingFaction");
    }

    public PlanetaryAssaultAccessor setAttackingFaction(Faction faction) {
        set("attackingFaction", faction);
        return this;
    }

    public Faction getDefendingFaction() {
        return getFaction("defendingFaction");
    }

    public PlanetaryAssaultAccessor setDefendingFaction(Faction faction) {
        set("defendingFaction", faction);
        return this;
    }

    public boolean isGameFull() {
        return (boolean) get("gameFull");
    }

    public PlanetaryAssaultAccessor setGameFull(boolean gameFull) {
        set("gameFull", gameFull);
        return this;
    }

    public Integer getAttackerCount() {
        return (Integer) get("attackerCount");
    }

    public Integer getDefenderCount() {
        return (Integer) get("defenderCount");
    }

    public PlanetaryAssaultAccessor setParticipantCount(BattleRole team, Integer count) {
        if (team == BattleRole.ATTACKER) {
            set("attackerCount", count);
        } else {
            set("defenderCount", count);
        }

        return this;
    }

    public Double getWaitingProgress() {
        return (Double) get("waitingProgress");
    }

    public PlanetaryAssaultAccessor setWaitingProgress(Double progress) {
        set("waitingProgress", progress);
        return this;
    }

    public GameResult getGameResult() {
        return (GameResult) get("gameResult");
    }

    public BattleRole getWinner() {
        return BattleRole.fromNameString(getString("winner"));
    }

    public PlanetaryAssaultAccessor setWinner(BattleRole winner) {
        set("winner", winner.getName());
        return this;
    }

    public String getErrorCode() {
        return getString("errorCode");
    }

    public String getErrorMessage() {
        return getString("errorMessage");
    }
}
