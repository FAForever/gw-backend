package com.faforever.gw.security;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class JsonAccessToken {
    @JsonProperty("authorities")
    List<String> authorities;
    @JsonProperty("user_id")
    private long userId;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("exp")
    private Instant expiresAt;
}
