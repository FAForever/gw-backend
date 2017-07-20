package com.faforever.gw.websocket;

import com.faforever.gw.security.User;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
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
        Principal principal = session.getPrincipal();

        boolean principalError = false;
        // WTF Spring? - PreAuthentication has the principal nested inside
        if (principal.getClass() == PreAuthenticatedAuthenticationToken.class) {
            PreAuthenticatedAuthenticationToken token = (PreAuthenticatedAuthenticationToken) principal;

            if (token.getPrincipal().getClass() == User.class) {
                User user = (User) token.getPrincipal();

                userIdToWebSocketSession.put(user.getId(), session);
                webSocketSessionIdToUser.put(session.getId(), user);
            } else
                principalError = true;
        } else {
            principalError = true;
        }

        if (principalError) {
            RuntimeException e = new RuntimeException("The user is not authenticated as expected.");
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    public Collection<WebSocketSession> getSessions() {
        return userIdToWebSocketSession.values();
    }

    public User getUser(WebSocketSession session) {
        return webSocketSessionIdToUser.getOrDefault(session.getId(), null);
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
