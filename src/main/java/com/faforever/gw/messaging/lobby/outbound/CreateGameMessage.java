package com.faforever.gw.messaging.lobby.outbound;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CreateGameMessage extends OutboundLobbyMessage {
    private UUID battleId;
    private List<Long> participants;
}
