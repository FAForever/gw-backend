package com.faforever.gw.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
public class GameCharacterResult implements Serializable {
    UUID character;
    BattleParticipantResult participantResult;
    UUID killedByCharacter;
//    Integer killedExperimentals;
}
