package com.faforever.gw.config;


import com.faforever.gw.security.AuthenticationInterceptorAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import javax.inject.Inject;


@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
    private final AuthenticationInterceptorAdapter authenticationInterceptorAdapter;

    @Inject
    public WebSocketConfig(AuthenticationInterceptorAdapter authenticationInterceptorAdapter) {
        this.authenticationInterceptorAdapter = authenticationInterceptorAdapter;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.setInterceptors(authenticationInterceptorAdapter);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/action");
        // only append new channels - don't call enableSimpleBroker twice - the second call overwrites the first
        config.enableSimpleBroker("/planets", "/battles");
//        config.enableSimpleBroker("/queue/", "/topic/", "/exchange/");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").setAllowedOrigins("*");//.withSockJS();
    }
}
