package com.faforever.gw.messaging.lobby.outbound;

import com.faforever.gw.messaging.lobby.LobbyMode;
import com.faforever.gw.model.Faction;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMatchRequest extends OutboundLobbyMessage {
    /* name of the game */
    private String title;
    /* map version id in FAF */
    private int map;
    private String featuredMod;
    private List<Participant> participants;
    private LobbyMode lobbyMode = LobbyMode.NONE;

    @Getter
    @Setter
    public static class Participant {
        private long id;
        private Faction faction;
        private int startSpot;
        private int team;
        private String name;
    }
}