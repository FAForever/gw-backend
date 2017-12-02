package com.faforever.gw.messaging.client;

import com.faforever.gw.messaging.client.inbound.*;
import com.faforever.gw.messaging.client.outbound.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@Getter
@AllArgsConstructor
public class ClientMessageWrapper {
    @JsonTypeInfo(use = Id.NAME, include = As.EXTERNAL_PROPERTY, property = "action")
    @JsonSubTypes({
            // Inbound messages
            @Type(value = InitiateAssaultMessage.class, name = "initiateAssault"),
            @Type(value = JoinAssaultMessage.class, name = "joinAssault"),
            @Type(value = LeaveAssaultMessage.class, name = "leaveAssault"),
            @Type(value = LinkSolarSystemsRequestMessage.class, name = "linkSolarSystemsRequest"),
            @Type(value = RequestCharacterMessage.class, name = "requestCharacter"),
            @Type(value = SelectCharacterNameMessage.class, name = "selectCharacterName"),
            @Type(value = SetPlanetFactionRequestMessage.class, name = "setPlanetFactionRequest"),
            @Type(value = UnlinkSolarSystemsRequestMessage.class, name = "unlinkSolarSystemsRequest"),

            // Outbound messages
            @Type(value = AckMessage.class, name = "ack"),
            @Type(value = BattleParticipantJoinedAssaultMessage.class, name = "battle.participant_joined"),
            @Type(value = BattleParticipantLeftAssaultMessage.class, name = "battle.participant_left"),
            @Type(value = BattleUpdateWaitingProgressMessage.class, name = "battle.waiting_progress"),
            @Type(value = CharacterJoinedGwMessage.class, name = "character.new"),
            @Type(value = CharacterNameProposalMessage.class, name = "character.name_proposal"),
            @Type(value = CharacterPromotionMessage.class, name = "character.promotion"),
            @Type(value = ErrorMessage.class, name = "error"),
            @Type(value = HelloMessage.class, name = "user.hello"),
            @Type(value = PlanetConqueredMessage.class, name = "planet.conquered"),
            @Type(value = PlanetDefendedMessage.class, name = "planet.defended"),
            @Type(value = PlanetOwnerChangedMessage.class, name = "universe.planet_owner_changed"),
            @Type(value = PlanetUnderAssaultMessage.class, name = "planet.attacked"),
            @Type(value = SolarSystemsLinkedMessage.class, name = "universe.solar_systems_linked"),
            @Type(value = SolarSystemsUnlinkedMessage.class, name = "universe.solar_systems_unlinked"),
            @Type(value = UserIncomeMessage.class, name = "user.income")
    })
    ClientMessage data;
}
