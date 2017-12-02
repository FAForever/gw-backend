package com.faforever.gw.messaging.lobby;

import com.faforever.gw.messaging.lobby.inbound.CreateGameResultMessage;
import com.faforever.gw.messaging.lobby.inbound.GameResultMessage;
import com.faforever.gw.messaging.lobby.outbound.CreateGameMessage;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@Getter
@AllArgsConstructor
public class LobbyMessageWrapper {
    @JsonTypeInfo(use = Id.NAME, include = As.EXTERNAL_PROPERTY, property = "action")
    @JsonSubTypes({
            // Inbound messages
            @Type(value = CreateGameResultMessage.class, name = "createGameResult"),
            @Type(value = GameResultMessage.class, name = "gameResult"),

            // Outbound messages
            @Type(value = CreateGameMessage.class, name = "createGame")
    })
    LobbyMessage data;
}
