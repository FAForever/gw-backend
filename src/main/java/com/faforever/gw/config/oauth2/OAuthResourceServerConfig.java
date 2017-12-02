package com.faforever.gw.config.oauth2;

import com.faforever.gw.config.GwServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import static org.springframework.security.oauth2.common.OAuth2AccessToken.ACCESS_TOKEN;
import static org.springframework.security.oauth2.common.OAuth2AccessToken.BEARER_TYPE;

/**
 * OAuth2 resource server configuration.
 */
@Slf4j
@Configuration
@EnableResourceServer
public class OAuthResourceServerConfig extends ResourceServerConfigurerAdapter {

    private final String resourceId;
    private final ResourceServerTokenServices tokenServices;
    private final TokenExtractor tokenExtractor;

    public OAuthResourceServerConfig(GwServerProperties serverProperties, ResourceServerTokenServices tokenServices, TokenExtractor tokenExtractor) {
        this.resourceId = serverProperties.getOAuth2().getResourceId();
        this.tokenServices = tokenServices;
        this.tokenExtractor = tokenExtractor;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(resourceId)
                .tokenServices(tokenServices)
                .tokenExtractor(tokenExtractor);
    }


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.requestMatcher(new OAuthRequestedMatcher())
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest().authenticated();
    }

    private static class OAuthRequestedMatcher implements RequestMatcher {
        public boolean matches(HttpServletRequest request) {
            String auth = request.getHeader("Authorization");
            boolean hasOauth2Token = (auth != null) && auth.toLowerCase(Locale.US).startsWith(BEARER_TYPE.toLowerCase());
            boolean hasAccessToken = request.getParameter(ACCESS_TOKEN) != null;
            return hasOauth2Token || hasAccessToken;
        }
    }
}
