package com.faforever.gw.services.messaging.client;

import com.faforever.gw.services.messaging.client.incoming.*;
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
    CHARACTER_NAME_PROPOSAL("character.name_proposal", Audience.PRIVATE, CharacterNameProposalMessage.class),
    CHARACTER_JOINED_GW("character.new", Audience.PUBLIC, CharacterJoinedGwMessage.class),
    //    FACTION_CHAT_MESSAGE("faction.chat_message", Audience.FACTION),
    USER_INCOME("user.income", Audience.PRIVATE, UserIncomeMessage.class),
//    USER_XP("user.xp", Audience.PRIVATE);
SOLAR_SYSTEMS_LINKED("universe.solar_systems_linked", Audience.PUBLIC, SolarSystemsLinkedMessage.class),
    SOLAR_SYSTEMS_UNLINKED("universe.solar_systems_unlinked", Audience.PUBLIC, SolarSystemsUnlinkedMessage.class),
    PLANET_OWNER_CHANGED("universe.planet_owner_changed", Audience.PUBLIC, PlanetOwnerChangedMessage.class),

    // Incoming messages (User actions) -->
    ACTION_INITIATE_ASSAULT("initiateAssault", null, InitiateAssaultMessage.class),
    ACTION_JOIN_ASSAULT("joinAssault", null, JoinAssaultMessage.class),
    ACTION_LEAVE_ASSAULT("leaveAssault", null, LeaveAssaultMessage.class),
    ACTION_REQUEST_CHARACTER("requestCharacter", null, RequestCharacterMessage.class),
    ACTION_SELECT_CHARACTER_NAME("selectCharacterName", null, SelectCharacterNameMessage.class),
    ADMIN_LINK_SOLAR_SYSTEMS_REQUEST("linkSolarSystemsRequest", null, LinkSolarSystemsRequestMessage.class),
    ADMIN_UNLINK_SOLAR_SYSTEMS_REQUEST("unlinkSolarSystemsRequest", null, UnlinkSolarSystemsRequestMessage.class),
    ADMIN_SET_PLANET_FACTION_REQUEST("setPlanetFactionRequest", null, SetPlanetFactionRequestMessage.class);

    @Getter(value = AccessLevel.NONE)
    private static final Map<String, Class> messageTypeByAction = new HashMap<>();
    private static final Map<Class, MessageType> messageTypeByClass = new HashMap<>();

    static {
        for (MessageType messageType : values()) {
            messageTypeByAction.put(messageType.getName(), messageType.getMessageClass());
            messageTypeByClass.put(messageType.getMessageClass(), messageType);
        }
    }

    private final String name;
    private final Audience audience;
    private final Class messageClass;

    public static Class getByAction(String action) {
        return messageTypeByAction.getOrDefault(action, null);
    }

    public static MessageType getByClass(Class clazz) {
        return messageTypeByClass.getOrDefault(clazz, null);
    }


    public enum Audience {
        PUBLIC,
        FACTION,
        PRIVATE
    }
}
