package com.faforever.gw.config.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.security.oauth2.common.OAuth2AccessToken.ACCESS_TOKEN;

@Slf4j
@Component
public class QueryParameterTokenExtractor extends BearerTokenExtractor {

    @Override
	public String extractToken(HttpServletRequest request) {
        String accessTokenString = request.getParameter(ACCESS_TOKEN);
        if (accessTokenString == null) {
			return super.extractToken(request);
        }

		return accessTokenString;
    }
}