package com.faforever.gw.websocket;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ParticipantRepository {

	private Map<String, LoginEvent> activeSessions = new ConcurrentHashMap<>();

	public void add(String sessionId, LoginEvent event) {
		activeSessions.put(sessionId, event);
	}

	public LoginEvent getParticipant(String sessionId) {
		return activeSessions.get(sessionId);
	}

	public void removeParticipant(String sessionId) {
		activeSessions.remove(sessionId);
	}

	public Map<String, LoginEvent> getActiveSessions() {
		return activeSessions;
	}

	public void setActiveSessions(Map<String, LoginEvent> activeSessions) {
		this.activeSessions = activeSessions;
	}
}
