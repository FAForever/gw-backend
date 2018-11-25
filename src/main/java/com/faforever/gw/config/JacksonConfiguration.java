package com.faforever.gw.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
public class JacksonConfiguration {

  @Inject
  public void objectMapper(ObjectMapper objectMapper) {
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
  }
}
