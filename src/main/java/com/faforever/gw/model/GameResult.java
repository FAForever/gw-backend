package com.faforever.gw.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

@Data
public class GameResult implements Serializable {
    UUID battle;
    Faction winner;
    Collection<GameCharacterResult> characterResults; // there must be one GameCharacterResult for each participant of the battle!
}
