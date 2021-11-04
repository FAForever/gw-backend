package com.faforever.gw.model;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public record BattleResult(
        @NotNull UUID battleId,
        @NotNull Optional<BattleRole> winningTeam,
        @NotNull java.util.Map<UUID, BattleParticipantResult> participantResults
) {
}
