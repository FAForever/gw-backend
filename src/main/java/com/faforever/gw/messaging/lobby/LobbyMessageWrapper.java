package com.faforever.gw.messaging.lobby;

import com.faforever.gw.messaging.lobby.inbound.*;
import com.faforever.gw.messaging.lobby.outbound.CreateMatchRequest;
import com.faforever.gw.messaging.lobby.outbound.PingMessage;
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
    @JsonTypeInfo(use = Id.NAME, include = As.EXTERNAL_PROPERTY, property = "type")
    @JsonSubTypes({
            // Inbound messages
            @Type(value = ErrorMessage.class, name = "error"),
            @Type(value = MatchCreatedMessage.class, name = "matchCreated"),
            @Type(value = GameResultMessage.class, name = "gameResult"),
            @Type(value = GameInfoMessage.class, name = "game"),
            @Type(value = PlayersMessage.class, name = "players"),

            // Outbound messages
            @Type(value = PingMessage.class, name = "ping"),
            @Type(value = CreateMatchRequest.class, name = "createMatch")
    })
    LobbyMessage data;
}
