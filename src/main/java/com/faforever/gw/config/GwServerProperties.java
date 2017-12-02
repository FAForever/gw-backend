package com.faforever.gw.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "faf-gw", ignoreUnknownFields = false)
public class GwServerProperties {
    private Jwt jwt = new Jwt();
    private OAuth2 oAuth2 = new OAuth2();

    @Data
    public static class OAuth2 {
        private String resourceId = "faf-gw";
    }

    @Data
    public static class Jwt {
        /**
         * The secret used for JWT token verification.
         */
        private String secret;
    }
}
