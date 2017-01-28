package com.faforever.gw.model;

import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;

@AllArgsConstructor
public enum GwError {
    CHARACTER_NOT_FREE_FOR_GAME("1001", "Character not free for game"),
    PLANET_PROTECTED_FROM_ASSAULT("1002", "Planet is currently protected from assaults"),
    PLANET_NOT_IN_REACH("1003", "Planet is not in reach of the attacker's faction"),
    NO_SLOTS_FOR_FACTION("1004", "There are no open slots in this battle for the character's faction");

    private final String errorCode;
    private final String message;

    public BpmnError asBpmnError(){
        return new BpmnError(errorCode, message);
    }
}
