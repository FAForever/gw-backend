package com.faforever.gw.services;

import com.faforever.gw.model.BattleRole;

/**
 * This map slot assigner assumes that all map slots for teams are split up with even/odd spots.
 * For the random map generator this is guaranteed. This does not apply for all other maps.
 */
public class MapSlotAssigner {
        private int nextAttackerSlot = 1;
        private int nextDefenderSlot = 2;

    private int getNextAttackerSlot() {
        int next = nextAttackerSlot;
        nextAttackerSlot += 2;
        return next;
    }

    private int getNextDefenderSlot() {
        int next = nextDefenderSlot;
        nextDefenderSlot += 2;
        return next;
    }

    public int nextSlot(BattleRole role) {
        return switch (role) {
            case ATTACKER -> getNextAttackerSlot();
            case DEFENDER -> getNextDefenderSlot();
        };
    }
}
