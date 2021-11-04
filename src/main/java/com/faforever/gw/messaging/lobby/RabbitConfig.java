package com.faforever.gw.messaging.lobby;

import com.faforever.gw.messaging.lobby.inbound.GameResultMessage;
import com.faforever.gw.messaging.lobby.inbound.MatchCreateError;
import com.faforever.gw.messaging.lobby.inbound.MatchCreateSuccess;
import com.faforever.gw.messaging.lobby.outbound.MatchCreateRequest;
import com.faforever.gw.messaging.lobby.outbound.MatchCreateRequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.springframework.amqp.support.AmqpHeaders.CORRELATION_ID;

@Configuration
@Slf4j
public class RabbitConfig {
    private final static String ROUTING_KEY_HEADER = "routingKey";
    private final static String ROUTING_KEY_LAUNCH_GAME_REQUEST = "request.match.create";

    private @NotNull Optional<UUID> getCorrelationId(@NotNull Message<?> message) {
        Object correlationHeader = message.getHeaders().get(CORRELATION_ID);

        if (correlationHeader == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(UUID.fromString(correlationHeader.toString()));
        } catch (IllegalArgumentException e) {
            log.error("Correlation header of message is no valid UUID: {}", correlationHeader);
            return Optional.empty();
        }
    }

    @Bean
    public MatchCreateRequestHandler matchCreateRequestHandler(StreamBridge streamBridge) {
        return request -> streamBridge.send(
                "createGameRequest-out-0",
                MessageBuilder.withPayload(request)
                        .setHeader(ROUTING_KEY_HEADER, ROUTING_KEY_LAUNCH_GAME_REQUEST)
                        .setHeader(CORRELATION_ID, request.requestId().toString())
                        .build()
        );
    }

    @Bean
    public Consumer<Message<MatchCreateSuccess>> onMatchCreateSuccess(LobbyService lobbyService) {
        return message -> {
            UUID requestId = getCorrelationId(message).orElseThrow(() -> new IllegalStateException("requestId missing"));

            log.trace("Received MatchCreateSuccess (request id {}): {}", requestId, message.getPayload());

            long gameId = message.getPayload().gameId();
            lobbyService.onGameCreated(requestId, gameId);
        };
    }

    @Bean
    public Consumer<Message<MatchCreateError>> onMatchCreateError(LobbyService lobbyService) {
        return message -> {
            UUID requestId = getCorrelationId(message).orElseThrow(() -> new IllegalStateException("requestId missing"));

            log.trace("Received MatchCreateError (request id {}): {}", requestId, message.getPayload());

            lobbyService.onGameCreationFailed(requestId, message.getPayload().errorCode(), message.getPayload().args());
        };
    }

    @Bean
    public Consumer<Message<GameResultMessage>> onGameResult(LobbyService lobbyService) {
        return message -> {
            log.trace("Received game result (game id {}): {}", message.getPayload().gameId(), message.getPayload());

            lobbyService.onGameResult(message.getPayload());
        };
    }
}
