package com.faforever.gw.messaging.lobby.inbound;

import lombok.Getter;
import lombok.Setter;

/**
 * Message sent from the server to the client containing information about a player.
 */
@Getter
@Setter
public class PlayerSubMessage {

    private int playerId;
    private String username;
    private String country;
    private Player player;

    @Getter
    @Setter
    static class Player {
        Rating globalRating;
        Rating ladder1v1Rating;
        int numberOfGames;
        Avatar avatar;
        String clanTag;

        @Getter
        @Setter
        static class Rating {
            double mean;
            double deviation;
        }

        @Getter
        @Setter
        static class Avatar {
            String url;
            String description;
        }
    }
}
