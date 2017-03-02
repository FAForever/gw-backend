package com.faforever.gw.bpmn.accessors;

import com.faforever.gw.model.BattleRole;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.GameResult;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.h2.table.Plan;

import java.util.Map;
import java.util.UUID;

public class PlanetaryAssaultAccessor extends BaseAccessor {
    private PlanetaryAssaultAccessor(DelegateExecution processContext) {
        super(processContext);
    }

    public UUID getInitiatorId() {
        return (UUID) get("initiator");
    }

    public UUID getBattleId() {
        return (UUID) get("battle");
    }

    public PlanetaryAssaultAccessor setBattleId(UUID battleId) {
        set("battle", battleId);
        return this;
    }

    public UUID getPlanetId() {
        return (UUID) get("planet");
    }

    public Faction getAttackingFaction() {
        return (Faction) get("attackingFaction");
    }

    public Faction getDefendingFaction() {
        return (Faction) get("defendingFaction");
    }

    public UUID getLastJoinedCharacter() {
        return (UUID) get("lastJoinedCharacter");
    }

    public UUID getLastLeftCharacter() {
        return (UUID) get("lastLeftCharacter");
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

    public PlanetaryAssaultAccessor setWaitingProgress(Double progress){
        set("waitingProgress", progress);
        return this;
    }

    public GameResult getGameResult() {
        return (GameResult) get("gameResult");
    }

    public BattleRole getWinner() {
        return BattleRole.fromNameString((String) get("winner"));
    }

    public PlanetaryAssaultAccessor setWinner(BattleRole winner) {
        set("winner", winner.getName());
        return this;
    }

    public UUID getErrorCharacter() {
        return (UUID) get("errorCharacter");
    }

    public PlanetaryAssaultAccessor setErrorCharacter(UUID errorCharacter) {
        set("errorCharacter", errorCharacter);
        return this;
    }

    public String getErrorCode() {
        return (String) get("errorCode");
    }

    public String getErrorMessage() {
        return (String) get("errorMessage");
    }

    public static PlanetaryAssaultAccessor of(DelegateExecution processContext) {
        return new PlanetaryAssaultAccessor(processContext);
    }
}
