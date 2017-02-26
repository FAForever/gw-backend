package com.faforever.gw.services.messaging;

import com.faforever.gw.model.Faction;
import com.faforever.gw.security.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WebsocketChannel {
    PLANETS_ATTACKED("/planets/attacked", Type.PUBLIC),
    PLANETS_CONQUERED("/planets/conquered", Type.PUBLIC),
    PLANETS_DEFENDED("/planets/defended", Type.PUBLIC),
    BATTLE_WAITING_PROGRESS("/battles/waiting_progress", Type.PUBLIC),
    BATTLES_PARTICIPANT_JOINED("/battles/participant_joined", Type.PUBLIC),
    BATTLES_PARTICIPANT_LEFT("/battles/participant_left", Type.PUBLIC),
    CHARACTERS_PROMOTIONS("/characters/promotions", Type.PUBLIC),
    FACTION_CHAT_MESSAGE("/faction/{factionId]/chat_message", Type.FACTION),
    USER_ERROR("/direct/error", Type.PRIVATE),
    USER_INCOME("/direct/income", Type.PRIVATE),
    USER_XP("/direct/xp", Type.PRIVATE);

    private final String channelName;
    private final Type type;

    public enum Type {
        PUBLIC,
        FACTION,
        PRIVATE
    }

    public String toUserString(User user) {
        return channelName.replace("{userId}", user.getName());
    }

    public String toFactionString(Faction faction) {
        return channelName.replace("{factionId}", faction.getName());
    }
}
