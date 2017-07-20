package com.faforever.gw.services.messaging.lobby_server;

import com.faforever.gw.services.messaging.lobby_server.outgoing.CreateGameMessage;
import com.faforever.gw.services.messaging.lobby_server.outgoing.OutgoingLobbyMessage;
import com.faforever.gw.websocket.WebSocketEnvelope;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class LobbyService {
    public static final String LOBBY_SERVER_WEBSOCKET_URI = "ws://localhost:9001";
    private final Runnable sendingTask;
    private final BlockingQueue<WebSocketMessage> messageQueue;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper jsonObjectMapper;
    private final Map<String, ActionFunc> actionMapping = new HashMap<>();
    private final WebSocketClient webSocketClient;
    private final TextWebSocketHandler webSocketHandler;
    private ThreadPoolTaskExecutor taskExecutor;
    private WebSocketSession currentSession;

    public LobbyService(ApplicationEventPublisher applicationEventPublisher, ObjectMapper jsonObjectMapper) {
        resetTaskExecutor();
        this.messageQueue = new ArrayBlockingQueue<>(50);

        this.applicationEventPublisher = applicationEventPublisher;
        this.jsonObjectMapper = jsonObjectMapper;
        this.webSocketClient = new StandardWebSocketClient();

        this.webSocketHandler = new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                log.debug("Lobby server connection established");
                taskExecutor.execute(sendingTask);
            }

            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                try {
                    WebSocketEnvelope envelope;

                    try {
                        envelope = jsonObjectMapper.readValue(message.getPayload(), WebSocketEnvelope.class);

                        if (envelope.getAction() == null)
                            throw new IOException();
                    } catch (Exception e) {
                        log.error("Invalid message envelope. Ignoring message.");
                        return;
                    }

                    val messageClass = LobbyServerMessageType.getByAction(envelope.getAction());
                    Object convertedMessage = jsonObjectMapper.readValue(envelope.getData(), messageClass);
                    log.debug("New message with type {} published", messageClass.getTypeName());
                    applicationEventPublisher.publishEvent(convertedMessage);
                } catch (Exception e) {
                    log.error("An error occured on message handling: ", e);
                    throw e;
                }
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                log.debug("Lobby server connection closed, trying to reconnect");
                resetTaskExecutor();
                connect(LOBBY_SERVER_WEBSOCKET_URI);
            }
        };

        this.sendingTask = () -> {
            try {
                while (true) {
                    WebSocketMessage message = messageQueue.take();
                    currentSession.sendMessage(message);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    private void resetTaskExecutor() {
        if (taskExecutor != null) {
            taskExecutor.shutdown();
        }

        taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.initialize();
    }

    @PostConstruct
    private void postConstruct() {
        connect(LOBBY_SERVER_WEBSOCKET_URI);
    }

    private CompletableFuture<WebSocketSession> connect(String uri) {
        return connect(uri, 20);
    }

    private CompletableFuture<WebSocketSession> connect(String uri, int reconnectAttemptsLeft) {
        CompletableFuture<WebSocketSession> completableFuture = new CompletableFuture<>();

        webSocketClient.doHandshake(webSocketHandler, uri)
                .addCallback(new ListenableFutureCallback<WebSocketSession>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        log.error("WebSocket handshake failed", throwable);

                        if (reconnectAttemptsLeft > 0) {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ignored) {
                            }
                            log.warn(MessageFormat.format("Could not connect to lobby server. Attempts left: {0} (uri={1})", reconnectAttemptsLeft, uri));
                            connect(uri, reconnectAttemptsLeft - 1);
                        } else {
                            RuntimeException e = new RuntimeException(MessageFormat.format("Could not connect to lobby server. (uri={0})", uri));
                            log.error(e.getMessage(), e);
                            completableFuture.completeExceptionally(throwable);
                        }

                    }

                    @Override
                    public void onSuccess(WebSocketSession session) {
                        log.info("WebSocket handshake with Lobby successful");
                        currentSession = session;
                        completableFuture.complete(session);
                    }
                });

        return completableFuture;
    }

    private TextMessage box(OutgoingLobbyMessage message) throws JsonProcessingException {
        return new TextMessage(
                jsonObjectMapper.writeValueAsString(
                        new WebSocketEnvelope(message.getAction().getName(), jsonObjectMapper.writeValueAsString(message))
                )
        );
    }

    private void enqueue(OutgoingLobbyMessage message) {
        try {
            messageQueue.add(box(message));
        } catch (JsonProcessingException e) {
            log.error("Error on converting message to string", e);
        }
    }

    public void createGame(UUID battleId) {
        enqueue(new CreateGameMessage(UUID.randomUUID(), battleId));
    }

    private interface ActionFunc {
        void processMessage(WebSocketEnvelope envelope);
    }
}
