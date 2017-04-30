package com.faforever.gw.config;

import com.faforever.gw.websocket.WebSocketInputHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.inject.Inject;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    WebSocketInputHandler webSocketInputHandler;

    @Inject
    public WebSocketConfig(WebSocketInputHandler webSocketInputHandler) {
        this.webSocketInputHandler = webSocketInputHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(webSocketInputHandler, "/websocket").setAllowedOrigins("*");
    }
}
