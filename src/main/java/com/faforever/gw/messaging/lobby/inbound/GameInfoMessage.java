package com.faforever.gw.messaging.lobby.inbound;


import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

/**
 * Message sent from the server to the client containing information about an available featured mod.
 */
@Getter
@Setter
public class GameInfoMessage extends InboundLobbyMessage {
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

    @Getter
    @Setter
    static class Player {
        int team;
        String name;
    }

    @Getter
    @Setter
    static class SimMod {
        String uid;
        String displayName;
    }

    @Getter
    @Setter
    static class FeaturedModFileVersion {
        short id;
        int version;
    }
}
