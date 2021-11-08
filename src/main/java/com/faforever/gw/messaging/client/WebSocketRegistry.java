package com.faforever.gw.messaging.client;

import com.faforever.gw.security.User;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class WebSocketRegistry {
    private Multimap<Long, WebSocketSession> userIdToWebSocketSession;
    private Map<String, User> webSocketSessionIdToUser = new HashMap<>();

    public WebSocketRegistry() {
        userIdToWebSocketSession = ArrayListMultimap.create();
    }

    public void add(WebSocketSession session) {
        Principal sessionPrincipal = session.getPrincipal();

        if (sessionPrincipal instanceof User user) {
            userIdToWebSocketSession.put(user.getId(), session);
            webSocketSessionIdToUser.put(session.getId(), user);
        } else {
            log.warn("unexpected principal");
        }
    }

    public Collection<WebSocketSession> getSessions() {
        return userIdToWebSocketSession.values();
    }

    public User getUser(WebSocketSession session) {
        return webSocketSessionIdToUser.getOrDefault(session.getId(), null);
    }

    public Collection<WebSocketSession> getSession(long fafUserId) {
        return userIdToWebSocketSession.get(fafUserId);
    }

    public Collection<WebSocketSession> getSession(User user) {
        return userIdToWebSocketSession.get(user.getId());
    }

    public void remove(WebSocketSession session) {
        long userId = webSocketSessionIdToUser.get(session.getId()).getId();
        userIdToWebSocketSession.remove(userId, session);
        webSocketSessionIdToUser.remove(session.getId());
    }
}
