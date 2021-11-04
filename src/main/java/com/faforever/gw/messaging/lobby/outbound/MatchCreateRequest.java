package com.faforever.gw.messaging.lobby.outbound;

import com.faforever.gw.model.Faction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public record MatchCreateRequest(
        @JsonIgnore
        UUID requestId,
        @JsonProperty("game_name")
        String gameName,
        @JsonProperty("map_name")
        String mapName,
        @JsonProperty("matchmaker_queue")
        String matchmakerQueue,
        List<Participant> participants
) implements OutboundLobbyMessage {
    public static record Participant(
            @JsonProperty("player_id")
            long playerId,
            Faction faction,
            int slot,
            int team,
            String name // TODO: not supported yet
    ) {
    }
}