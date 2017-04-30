package com.faforever.gw.websocket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class WebSocketEnvelope {
    @JsonProperty
    @NotNull
    private String action;
    @JsonRawValue
    @JsonDeserialize(using = RawJsonDeserializer.class)
    @NotNull
    private String data;
}
