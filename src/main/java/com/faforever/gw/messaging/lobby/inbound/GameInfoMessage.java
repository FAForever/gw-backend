package com.faforever.gw.messaging.lobby.inbound;


import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.time.Instant;
import java.util.List;

/**
 * Message sent from the server to the client containing information about an available featured mod.
 */
@Getter
@Setter
public class GameInfoMessage implements InboundLobbyMessage {
    int id;
    String title;
    String gameVisibility; // TODO: Convert back to enum
    Object password;
    String state;// TODO: Convert back to enum
    String featuredModTechnicalName;
    List<SimMod> simMods;
    String technicalMapName;
    String hostUsername;
    List<Player> players;
    int maxPlayers;
    Instant startTime;
    Integer minRating;
    Integer maxRating;
    List<FeaturedModFileVersion> featuredModFileVersions;

    @Value
    static class Player {
        int team;
        String name;
    }

    @Value
    static class SimMod {
        String uid;
        String displayName;
    }

    @Value
    static class FeaturedModFileVersion {
        short id;
        int version;
    }
}
