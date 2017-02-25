package com.faforever.gw.bpmn.accessors;

import com.faforever.gw.model.BattleRole;
import com.faforever.gw.model.Faction;

import java.util.Map;
import java.util.UUID;

public class PlanetaryAssaultAccessor {
    private final Map<String, Object> processVariables;

    private PlanetaryAssaultAccessor(Map<String, Object> processVariables) {
        this.processVariables = processVariables;
    }

    public UUID getInitiatorId() {
        return (UUID) processVariables.get("initiator");
    }

    public UUID getBattleId() {
        return (UUID) processVariables.get("battle");
    }

    public UUID getPlanetId() {
        return (UUID) processVariables.get("planet");
    }

    public Faction getAttackingFaction() {
        return (Faction) processVariables.get("attackingFaction");
    }

    public Faction getDefendingFaction() {
        return (Faction) processVariables.get("defendingFaction");
    }

    public UUID getLastJoinedCharacter() {
        return (UUID) processVariables.get("lastJoinedCharacter");
    }

    public UUID getLastLeftCharacter() {
        return (UUID) processVariables.get("lastLeftCharacter");
    }

    public boolean isGameFull() {
        return (boolean) processVariables.get("gameFull");
    }

    public Integer getAttackerCount() {return (Integer)processVariables.get("attackerCount");}
    public Integer getDefenderCount() {return (Integer)processVariables.get("defenderCount");}

    public Double getWaitingProgress() { return (Double) processVariables.get("waitingProgress"); }

    public BattleRole getWinner() {
        return BattleRole.fromNameString((String) processVariables.get("winner"));
    }

    public UUID getErrorCharacter() {
        return (UUID) processVariables.get("errorCharacter");
    }

    public String getErrorCode() {
        return (String) processVariables.get("errorCode");
    }

    public String getErrorMessage() {
        return (String) processVariables.get("errorMessage");
    }

    public static PlanetaryAssaultAccessor of(Map<String, Object> processVariables) {
        return new PlanetaryAssaultAccessor(processVariables);
    }
}
