package com.faforever.gw.config.oauth2;

import com.faforever.gw.config.GwServerProperties;
import com.faforever.gw.security.FafUserAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
public class OAuthJwtConfig {

    private final GwServerProperties properties;

    public OAuthJwtConfig(GwServerProperties properties) {
        this.properties = properties;
    }

    @Bean
    protected JwtAccessTokenConverter jwtAccessTokenConverter(FafUserAuthenticationConverter fafUserAuthenticationConverter) {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(properties.getJwt().getSecret());
        ((DefaultAccessTokenConverter) jwtAccessTokenConverter.getAccessTokenConverter()).setUserTokenConverter(fafUserAuthenticationConverter);
        return jwtAccessTokenConverter;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices(TokenStore tokenStore) {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore);
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }
}
