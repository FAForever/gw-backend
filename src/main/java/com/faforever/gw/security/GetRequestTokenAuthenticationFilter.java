package com.faforever.gw.security;

import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.service.CharacterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GetRequestTokenAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {
    private final MacSigner macSigner = new MacSigner("secret");
    private final ObjectMapper jsonObjectMapper;
    private final CharacterService characterService;

    @Inject
    public GetRequestTokenAuthenticationFilter(ObjectMapper jsonObjectMapper, CharacterService characterService) {
        this.jsonObjectMapper = jsonObjectMapper;
        this.characterService = characterService;

//        setCheckForPrincipalChanges(true);

        setAuthenticationManager(authentication -> {
            authentication.setAuthenticated(true);
            return authentication;
        });
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String accessTokenString = request.getParameter("accessToken");
        if (accessTokenString == null) {
            log.error("No access token in connection string");
            return request.getUserPrincipal();
        }

        JsonAccessToken accessToken;
        try {
            Jwt jwt = JwtHelper.decodeAndVerify(accessTokenString, macSigner);
            accessToken = jsonObjectMapper.readValue(jwt.getClaims(), JsonAccessToken.class);
        } catch (Exception e) {
            log.error("JWT could not be mapped to JsonAccessToken - probably malformed", e);
            return request.getUserPrincipal();
        }

        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
        accessToken.getAuthorities().forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role)));

        GwCharacter character = characterService.getByFafId(accessToken.getUserId()); // TODO: Check for the !one! active character of the user!

        return new User(accessToken.getUserId(), character, accessToken.getUserName(), "N/A", grantedAuthorities);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }
}
