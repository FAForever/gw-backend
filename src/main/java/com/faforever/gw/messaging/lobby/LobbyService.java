package com.faforever.gw.messaging.lobby;

import com.faforever.gw.messaging.lobby.inbound.GameResultMessage;
import com.faforever.gw.messaging.lobby.outbound.MatchCreateRequest;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.BattleParticipant;
import com.faforever.gw.model.BattleRole;
import com.faforever.gw.services.MapSlotAssigner;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LobbyService {
    private final ApplicationEventPublisher applicationEventPublisher;

    private final Map<UUID, Pair<Battle, CompletableFuture<Battle>>> pendingGames = new ConcurrentHashMap<>();
    private final Map<Long, Battle> runningGames = new ConcurrentHashMap<>();

    public CompletableFuture<Battle> createGame(@NotNull Battle battle) {
        MapSlotAssigner mapSlotAssigner = new MapSlotAssigner();

        List<MatchCreateRequest.Participant> participants = battle.getParticipants().stream()
                .map(battleParticipant -> new MatchCreateRequest.Participant(
                        battleParticipant.getCharacter().getFafId(),
                        battleParticipant.getFaction(),
                        mapSlotAssigner.nextSlot(battleParticipant.getRole()),
                        battleParticipant.getRole() == BattleRole.ATTACKER ? 1 : 2,
                        battleParticipant.getCharacter().getName()
                ))
                .collect(Collectors.toList());

        UUID requestId = UUID.randomUUID();
        MatchCreateRequest createMatchRequest = new MatchCreateRequest(
                requestId,
                "Galactic War battle " + battle.getId(),
                battle.getPlanet().getMap().toString(), // TODO: Determine map name
                "galactic_war", // TODO: Add this queue to lobby server
                participants
        );

        // TODO: Send message to RabbitMQ

        CompletableFuture<Battle> future = new CompletableFuture<>();
        pendingGames.put(requestId, Pair.of(battle, future));
        return future;
    }

    public void onGameCreated(@NotNull UUID requestId, long gameId) {
        Pair<Battle, CompletableFuture<Battle>> referredBattlePair = pendingGames.remove(requestId);

        if (referredBattlePair == null) {
            // This could be a game requested by a different service
            log.debug("Request id {} is unknown, silently ignoring game id {}", requestId, gameId);
        } else {
            log.info("Game id {} created successfully for request id {}", gameId, requestId);

            Battle battle = referredBattlePair.getFirst();
            CompletableFuture<Battle> battleFuture = referredBattlePair.getSecond();

            runningGames.put(gameId, battle);
            battleFuture.complete(battle);

        }
    }

    public void onGameCreationFailed(@NotNull UUID requestId, @NotNull String errorCode, Object args) {
        Pair<Battle, CompletableFuture<Battle>> referredBattlePair = pendingGames.remove(requestId);

        if (referredBattlePair == null) {
            // This could be a game requested by a different service
            log.debug("Request id {} is unknown, silently ignoring", requestId);
        } else {
            log.error("Game for request id {} failed to launch with code {} (args: {})", requestId, errorCode, args);

            Battle battle = referredBattlePair.getFirst();
            CompletableFuture<Battle> battleFuture = referredBattlePair.getSecond();

            battleFuture.completeExceptionally(new IllegalStateException("Failed to launch with error " + errorCode + "! Args: " + args == null ? "no args" : args.toString()));
        }
    }

    public void onGameResult(@NotNull GameResultMessage gameResultMessage) {
        Battle battle = runningGames.remove(gameResultMessage.gameId());

        if(battle == null) {
            // This could be a game requested by a different service
            log.debug("Game id {} is unknown, silently ignoring", gameResultMessage.gameId());
        } else {
            log.debug("Game id {} ended with results: {}", gameResultMessage.gameId(), gameResultMessage);
            applicationEventPublisher.publishEvent(gameResultMessage);
        }
    }
}
