package com.faforever.gw.services.messaging.lobby_server;

import com.faforever.gw.services.messaging.lobby_server.incoming.CreateGameResultMessage;
import com.faforever.gw.services.messaging.lobby_server.incoming.GameResultMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum LobbyServerMessageType {
    // Outgoing messaging -->
    CREATE_GAME("createGame", CreateGameResultMessage.class),

    // Incoming messaging (User actions) -->
    CREATE_GAME_Result("createGameResult", CreateGameResultMessage.class),
    GAME_RESULT("gameResult", GameResultMessage.class);


    @Getter(value = AccessLevel.NONE)
    private static final Map<String, Class> messageTypeByAction = new HashMap<>();

    static {
        for (LobbyServerMessageType lobbyServerMessageType : values()) {
            messageTypeByAction.put(lobbyServerMessageType.getName(), lobbyServerMessageType.getMessageClass());
        }
    }

    private final String name;
    private final Class messageClass;

    public static Class getByAction(String action) {
        return messageTypeByAction.getOrDefault(action, null);
    }

}
