package com.faforever.gw.messaging.lobby;

import com.faforever.gw.config.GwServerProperties;
import com.faforever.gw.messaging.lobby.inbound.ErrorMessage;
import com.faforever.gw.messaging.lobby.inbound.MatchCreatedMessage;
import com.faforever.gw.messaging.lobby.inbound.ResponseMessage;
import com.faforever.gw.messaging.lobby.inbound.ServerErrorException;
import com.faforever.gw.messaging.lobby.outbound.CreateMatchRequest;
import com.faforever.gw.messaging.lobby.outbound.OutboundLobbyMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
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
    private final GwServerProperties properties;
    private final Runnable sendingTask;
    private final BlockingQueue<WebSocketMessage> messageQueue;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper jsonObjectMapper;
    private final WebSocketClient webSocketClient;
    private final TextWebSocketHandler webSocketHandler;
    private ThreadPoolTaskExecutor taskExecutor;
    private WebSocketSession currentSession;
    private final Map<UUID, CompletableFuture<ResponseMessage>> pendingRequests;

    @Scheduled(fixedDelay = 30000)
    @SneakyThrows
    public void sendPing() {
        if (currentSession != null && currentSession.isOpen()) {
            log.debug("Send ping to lobby server");
            currentSession.sendMessage(new PingMessage());
        }
    }

    public LobbyService(GwServerProperties properties, ApplicationEventPublisher applicationEventPublisher, ObjectMapper jsonObjectMapper) {
        this.properties = properties;
        resetTaskExecutor();
        this.messageQueue = new ArrayBlockingQueue<>(50);
        pendingRequests = new HashMap<>();

        this.applicationEventPublisher = applicationEventPublisher;
        this.jsonObjectMapper = jsonObjectMapper;
        this.webSocketClient = new StandardWebSocketClient();

        this.webSocketHandler = new TextWebSocketHandler() {

            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                log.debug("Lobby server connection established");
                taskExecutor.execute(sendingTask);
            }

            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                log.trace("Incoming lobby message: {}", message.getPayload());
                LobbyMessage lobbyMessage;

                try {
                    LobbyMessageWrapper wrapper = jsonObjectMapper.readValue(message.getPayload(), LobbyMessageWrapper.class);
                    lobbyMessage = wrapper.getData();

                    if (lobbyMessage instanceof ResponseMessage) {
                        ResponseMessage response = (ResponseMessage) lobbyMessage;

                        if (pendingRequests.containsKey(response.getRequestId())) {
                            log.debug("Response to request id ''{}'' received", response.getRequestId());
                            pendingRequests.get(response.getRequestId()).complete(response);
                        } else {
                            log.error("Received response to unknown request id: ", response.getRequestId());
                        }
                    } else {
                        log.debug("New message with type {} published", lobbyMessage.getClass());
                        applicationEventPublisher.publishEvent(lobbyMessage);
                    }

                } catch (IOException e) {
                    log.error("An error occured on message handling: ", e);
                }
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
                log.debug("Lobby server connection closed, trying to reconnect. CloseStatus: {}", status);
                resetTaskExecutor();
                connect(properties.getLobby().getConnectionString());
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
        connect(properties.getLobby().getConnectionString());
    }

    private CompletableFuture<WebSocketSession> connect(String uri) {
        return connect(uri, 20);
    }

    @SneakyThrows
    private CompletableFuture<WebSocketSession> connect(String uri, int reconnectAttemptsLeft) {
        CompletableFuture<WebSocketSession> completableFuture = new CompletableFuture<>();

        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        MacSigner macSigner = new MacSigner("banana");
        Jwt token = JwtHelper.encode("{\"expires\":4102358400, \"authorities\": [\"ROLE_USER\"], \"client_id\": \"faf-gw-backend\"}", macSigner);
        headers.set("Authorization", "Bearer " + token.getEncoded());
        webSocketClient.doHandshake(webSocketHandler, headers, new URI(uri))
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

    private TextMessage box(LobbyMessage message) throws JsonProcessingException {
        return new TextMessage(jsonObjectMapper.writeValueAsString(new LobbyMessageWrapper(message)));
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private <T extends ResponseMessage> CompletableFuture<T> enqueue(OutboundLobbyMessage message) {
        TextMessage boxedMessage = box(message);
        CompletableFuture<T> future = new CompletableFuture<>();
        pendingRequests.put(message.getRequestId(), (CompletableFuture<ResponseMessage>) future);
        messageQueue.add(boxedMessage);

        return future;
    }

    public CompletableFuture<MatchCreatedMessage> createGame(CreateMatchRequest createMatchRequest) {
        return enqueue(createMatchRequest);
    }

    @EventListener
    private void onErrorResponse(ErrorMessage message) {
        log.error("Error ''{}'': {}\n{}", message.getCode(), message.getTitle(), message.getText());

        pendingRequests.get(message.getRequestId()).completeExceptionally(
                new ServerErrorException(message.getCode(), message.getTitle(), message.getText())
        );
    }
}
