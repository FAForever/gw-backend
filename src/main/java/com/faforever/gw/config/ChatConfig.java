package com.faforever.gw.config;

import com.faforever.gw.websocket.ParticipantRepository;
import com.faforever.gw.websocket.WebsocketEventListener;
import org.springframework.context.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class ChatConfig {

	public static class Destinations {
		private Destinations() {
		}

		private static final String LOGIN = "/topic/chat.login";
		private static final String LOGOUT = "/topic/chat.logout";
	}

	private static final int MAX_PROFANITY_LEVEL = 5;

	/*
	 * @Bean
	 * 
	 * @Description("Application event multicaster to process events asynchonously"
	 * ) public ApplicationEventMulticaster applicationEventMulticaster() {
	 * SimpleApplicationEventMulticaster multicaster = new
	 * SimpleApplicationEventMulticaster();
	 * multicaster.setTaskExecutor(Executors.newFixedThreadPool(10)); return
	 * multicaster; }
	 */
	@Bean
	@Description("Tracks user presence (join / leave) and broacasts it to all connected users")
	public WebsocketEventListener presenceEventListener(SimpMessagingTemplate messagingTemplate) {
		WebsocketEventListener presence = new WebsocketEventListener(messagingTemplate, participantRepository());
		presence.setLoginDestination(Destinations.LOGIN);
		presence.setLogoutDestination(Destinations.LOGOUT);
		return presence;
	}

	@Bean
	@Description("Keeps connected users")
	public ParticipantRepository participantRepository() {
		return new ParticipantRepository();
	}

}
