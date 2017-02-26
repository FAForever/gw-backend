package com.faforever.gw.model;

import javafx.util.Pair;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

@Data
public class GameResult implements Serializable {
    UUID battle;
    java.util.Map<UUID, GameCharacterResult> characterResults;
    Collection<Pair<UUID, UUID>> characterKills;
}
