package com.faforever.gw.config.elide;

import com.yahoo.elide.core.utils.coerce.converters.ElideTypeConverter;
import com.yahoo.elide.core.utils.coerce.converters.Serde;

import java.time.Duration;

@ElideTypeConverter(type = Duration.class, name = "Duration")
public class DurationSerde implements Serde<String, Duration> {
    @Override
    public Duration deserialize(String val) {
        return Duration.parse(val);
    }

    @Override
    public String serialize(Duration val) {
        return val.toString();
    }
}
