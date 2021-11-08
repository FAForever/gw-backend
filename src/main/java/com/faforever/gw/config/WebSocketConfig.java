package com.faforever.gw.config;

import com.faforever.gw.messaging.client.WebSocketInputHandler;
import com.faforever.gw.security.User;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketInputHandler webSocketInputHandler;
    private final JwtDecoder jwtDecoder;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
                .addHandler(webSocketInputHandler, "/websocket")
                .setAllowedOrigins("*")
                .setHandshakeHandler(handshakeHandler(jwtDecoder));
    }

    @Bean
    public HandshakeHandler handshakeHandler(JwtDecoder jwtDecoder) {
        return new DefaultHandshakeHandler() {
            @Override
            protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                if (request instanceof ServletServerHttpRequest servletServerHttpRequest) {
                    var servletRequest = servletServerHttpRequest.getServletRequest();
                    String accessToken = servletRequest.getParameter("access_token");
                    Jwt jwt = jwtDecoder.decode(accessToken);

                    long userId = Long.parseLong(jwt.getClaim("sub"));
                    JSONObject jwtExt = jwt.getClaim("ext");
                    JSONArray jwtRoles = (JSONArray) jwtExt.get("roles");

                    List<String> roles = jwtRoles.stream().map(x -> (String) x).toList();
                    return new User(userId, Optional.empty(), roles);
                } else {
                    throw new IllegalStateException("Wrong request type: " + request.getClass());
                }
            }
        };
    }
}
