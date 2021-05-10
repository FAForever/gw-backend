package com.faforever.gw.security;


import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;

@Component
@RequiredArgsConstructor
public class FafUserAuthenticationConverter extends DefaultUserAuthenticationConverter {
    private static final String ID = "user_id";
    private static final String NON_LOCKED = "non_locked";


    private final CharacterService characterService;

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        throw new UnsupportedOperationException("The server is not meant to generate JWT, only to parse them.");
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (!map.containsKey(ID)) {
            return null;
        }

        long userId = Long.valueOf((Integer) map.get(ID));
        String userName = (String) map.get(USERNAME);
        Collection<? extends GrantedAuthority> authorities = getAuthorities(map);

        GwCharacter character = characterService.getActiveCharacterByFafId(userId).orElse(null);
        return new User(userId, character, userName, "N/A", authorities);
//        return new GwAuthentication(userId, userName, authorities);
    }

    protected Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        if (!map.containsKey(AUTHORITIES)) {
            return Set.of();
        }
        Object authorities = map.get(AUTHORITIES);
        if (authorities instanceof String) {
            return commaSeparatedStringToAuthorityList((String) authorities);
        }
        if (authorities instanceof Collection) {
            return commaSeparatedStringToAuthorityList(StringUtils.collectionToCommaDelimitedString((Collection<?>) authorities));
        }
        throw new IllegalArgumentException("Authorities must be either a String or a Collection");
    }
}
