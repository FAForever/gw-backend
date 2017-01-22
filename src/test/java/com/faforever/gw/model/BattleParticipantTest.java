package com.faforever.gw.model;

import com.faforever.gw.validation.ValidateCharacterFreeForBattle;
import org.junit.Test;

public class BattleParticipantTest {
    @Test
    public void testAnnotations() {
        assert BattleParticipant.class.isAnnotationPresent(ValidateCharacterFreeForBattle.class);
    }
}