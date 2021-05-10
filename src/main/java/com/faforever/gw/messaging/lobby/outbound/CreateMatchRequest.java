package com.faforever.gw.messaging.lobby.outbound;

import com.faforever.gw.messaging.lobby.LobbyMode;
import com.faforever.gw.model.Faction;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class CreateMatchRequest implements OutboundLobbyMessage {
    UUID requestId = UUID.randomUUID();

    /* name of the game */
    String title;
    /* map version id in FAF */
    int map;
    String featuredMod;
    List<Participant> participants;
    LobbyMode lobbyMode = LobbyMode.NONE;

    @Value
    public static class Participant {
        long id;
        Faction faction;
        int startSpot;
        int team;
        String name;
    }
}