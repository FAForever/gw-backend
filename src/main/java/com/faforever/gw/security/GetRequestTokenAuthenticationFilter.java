package com.faforever.gw.security;

import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.service.CharacterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class GetRequestTokenAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {
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
        } catch (Exception e) {
            log.error("JWT could not be mapped to JsonAccessToken - probably malformed", e);
            return request.getUserPrincipal();
        }

        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
//        accessToken.getAuthorities().forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role)));
//
//        Optional<GwCharacter> character = characterService.getActiveCharacterByFafId(accessToken.getUserId());
//
//        return new User(accessToken.getUserId(), character, accessToken.getUserName(), "N/A", grantedAuthorities);
        return null;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }
}
