package com.faforever.gw.bpmn.services;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
/**
 * GW errors as used in all BPMN processes
 */
public enum GwErrorType {
    // Planet related error
    PLANET_DOES_NOT_EXIST("1001", "The given planet does not exist"),
    // Battle related errors
    BATTLE_INVALID("2001", "The given battle does not exist or is not accessible"),
    CHARACTER_NOT_FREE_FOR_BATTLE("2002", "Character not free for game"),
    CHARACTER_ALREADY_IN_BATTLE("2003", "Character is already participating a battle"),
    CHARACTER_NOT_IN_BATTLE("2004", "Character is not participant in this battle"),
    NO_SLOTS_FOR_FACTION("2005", "There are no open slots in this battle for the character's faction"),
    PLANET_PROTECTED_FROM_ASSAULT("2006", "Planet is currently protected from assaults"),
    PLANET_NOT_IN_REACH("2007", "Planet is not in reach of the attacker's faction"),
    PLANET_OWNED_BY_CHARACTERS_FACTION("2008", "Planet is already owned by characters faction");

    private final String errorCode;
    private final String errorMessage;
}
