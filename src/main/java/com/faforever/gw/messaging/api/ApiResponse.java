package com.faforever.gw.messaging.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class ApiResponse {
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static final ObjectMapper mapper = new ObjectMapper();

	public String toJson() {
		try {
			return mapper.writeValueAsString(this);
		} catch(JsonProcessingException e) {
			log.warn("Error while converting ApiResponse to json.", e);
			return "{\"error\":\"Error while converting ApiResponse to json.\"}";
		}
	}

	@Override
	public String toString() {
		return toJson();
	}
}
