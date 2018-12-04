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
    PLANET_OWNED_BY_CHARACTERS_FACTION("2008", "Planet is already owned by characters faction"),
    // Character related error
    NO_CREATION_WITH_ACTIVE_CHARACTER("3001", "You can't create a new character if you have an active one"),
    NO_CREATION_WITH_FACTION_MISMATCH("3002", "You can't change your faction during the season"),
    NO_CREATION_WITH_INVALID_SELECTION("3003", "Your chosen character name was not offered"),
    ALREADY_LINKED("4001", "The given solar systems are already linked"),
    NOT_LINKED("4002", "The given solar systems aren't linked"),
    NO_ACTIVE_CHARACTER("4100", "No currently active character found"),
    //Reinforcements related error
    REINFORCEMENT_INVALID("5001", "The given reinforcement does not exist."),
    NOT_ENOUGH_CREDITS("5002", "Not enough credits owned to complete transaction."),
    STRUCTURE_INVALID("5003", "The given defense structure does not exist."),
    PLANET_NOT_CONTROLLED("5003", "Planet not under your factions control."),
    NOT_ENOUGH_REINFORCEMENTS("5004", "Not enough reinforcements to complete or reinforcements invalid.");

    private final String errorCode;
    private final String errorMessage;
}
