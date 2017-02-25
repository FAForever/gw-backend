package com.faforever.gw.config;

import com.faforever.gw.websocket.ParticipantRepository;
import com.faforever.gw.websocket.WebsocketEventListener;
import org.springframework.context.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.inject.Inject;

@Configuration
public class ChatConfig {
	private final ParticipantRepository participantRepository;

	@Inject
	public ChatConfig(ParticipantRepository participantRepository) {
		this.participantRepository = participantRepository;
	}

	public static class Destinations {
		private Destinations() {
		}

		private static final String LOGIN = "/topic/chat.login";
		private static final String LOGOUT = "/topic/chat.logout";
	}

	private static final int MAX_PROFANITY_LEVEL = 5;
}
