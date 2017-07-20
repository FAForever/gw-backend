package com.faforever.gw.services.messaging.client;

import com.faforever.gw.services.messaging.client.incoming.InitiateAssaultMessage;
import com.faforever.gw.services.messaging.client.incoming.JoinAssaultMessage;
import com.faforever.gw.services.messaging.client.incoming.LeaveAssaultMessage;
import com.faforever.gw.services.messaging.client.outgoing.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum MessageType {
    // Outgoing messages -->
    ACK("ack", Audience.PRIVATE, AckMessage.class),
    ERROR("error", Audience.PRIVATE, ErrorMessage.class),
    PLANET_ATTACKED("planet.attacked", Audience.PUBLIC, PlanetUnderAssaultMessage.class),
    PLANET_CONQUERED("planet.conquered", Audience.PUBLIC, PlanetConqueredMessage.class),
    PLANET_DEFENDED("planet.defended", Audience.PUBLIC, PlanetDefendedMessage.class),
    BATTLE_WAITING_PROGRESS("battle.waiting_progress", Audience.PUBLIC, BattleUpdateWaitingProgressMessage.class),
    BATTLE_PARTICIPANT_JOINED("battle.participant_joined", Audience.PUBLIC, BattleParticipantJoinedAssaultMessage.class),
    BATTLE_PARTICIPANT_LEFT("battle.participant_left", Audience.PUBLIC, BattleParticipantLeftAssaultMessage.class),
    CHARACTER_PROMOTION("character.promotion", Audience.PUBLIC, CharacterPromotionMessage.class),
    HELLO("user.hello", Audience.PRIVATE, HelloMessage.class),
    //    FACTION_CHAT_MESSAGE("faction.chat_message", Audience.FACTION),
    USER_INCOME("user.income", Audience.PRIVATE, UserIncomeMessage.class),
//    USER_XP("user.xp", Audience.PRIVATE);

    // Incoming messages (User actions) -->
    ACTION_INITIATE_ASSAULT("initiateAssault", null, InitiateAssaultMessage.class),
    ACTION_JOIN_ASSAULT("joinAssault", null, JoinAssaultMessage.class),
    ACTION_LEAVE_ASSAULT("leaveAssault", null, LeaveAssaultMessage.class);

    @Getter(value = AccessLevel.NONE)
    private static final Map<String, Class> messageTypeByAction = new HashMap<>();

    static {
        for (MessageType messageType : values()) {
            messageTypeByAction.put(messageType.getName(), messageType.getMessageClass());
        }
    }

    private final String name;
    private final Audience audience;
    private final Class messageClass;

    public static Class getByAction(String action) {
        return messageTypeByAction.getOrDefault(action, null);
    }

    public enum Audience {
        PUBLIC,
        FACTION,
        PRIVATE
    }
}
