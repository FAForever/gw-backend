package com.faforever.gw.config.oauth2;

import com.faforever.gw.config.GwServerProperties;
import com.faforever.gw.security.JsonAccessToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.springframework.security.oauth2.common.OAuth2AccessToken.ACCESS_TOKEN;

@Slf4j
@Component
public class QueryParameterTokenExtractor implements TokenExtractor {
    private final ObjectMapper objectMapper;
    private final MacSigner macSigner;

    public QueryParameterTokenExtractor(GwServerProperties properties, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.macSigner = new MacSigner(properties.getJwt().getSecret());
    }

    @Override
    public Authentication extract(HttpServletRequest request) {
        String accessTokenString = request.getParameter(ACCESS_TOKEN);
        if (accessTokenString == null) {
            return null;
        }

        try {
            Jwt jwt = JwtHelper.decodeAndVerify(accessTokenString, macSigner);
            JsonAccessToken accessToken = objectMapper.readValue(jwt.getClaims(), JsonAccessToken.class);
            return new PreAuthenticatedAuthenticationToken(accessTokenString, null);
        } catch (IOException e) {
            log.error("JWT could not be mapped to JsonAccessToken - probably malformed", e);
            throw new PreAuthenticatedCredentialsNotFoundException("No access token in connection string");
        }
    }
}