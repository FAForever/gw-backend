package com.faforever.gw.messaging.lobby.inbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record GameResultMessage(
        @JsonProperty("game_id")
        long gameId,
        @JsonProperty("rating_type")
        String ratingType,
        String map,
        @JsonProperty("featured_mod")
        String featuredMod,
        @JsonProperty("sim_mod_ids")
        List<String> simModIds,
        @JsonProperty("commander_kills")
        Map<String, Integer> commanderKills,
        String validity,
        List<TeamResult> teams
) implements InboundLobbyMessage {

    public static record TeamResult(
            GameOutcome outcome,
            @JsonProperty("player_ids")
            List<Integer> playerIds,
            @JsonProperty("army_results")
            List<ArmyResult> armyResults
    ) {}

    public static record ArmyResult(
            @JsonProperty("player_id")
            long playerId,
            int army,
            @JsonProperty("army_outcome")
            ArmyOutcome armyOutcome,
            List<Object> metadata
    ) {
    }
}