package com.faforever.gw.model;

import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;

@AllArgsConstructor
public enum GwError {
    CHARACTER_NOT_FREE_FOR_BATTLE("1001", "Character not free for game"),
    CHARACTER_ALREADY_IN_BATTLE("1002", "Character is already participating in this battle"),
    CHARACTER_NOT_IN_BATTLE("1003", "Character is not participant in this battle"),
    PLANET_PROTECTED_FROM_ASSAULT("2001", "Planet is currently protected from assaults"),
    PLANET_NOT_IN_REACH("2002", "Planet is not in reach of the attacker's faction"),
    PLANET_OWNED_BY_CHARACTERS_FACTION("2003", "Planet is already owned by characters faction"),
    NO_SLOTS_FOR_FACTION("1006", "There are no open slots in this battle for the character's faction");

    private final String errorCode;
    private final String errorMessage;

    public BpmnError asBpmnError(){
        return new BpmnError(errorCode, errorMessage);
    }
}
