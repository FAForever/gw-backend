package com.faforever.gw.security;

import com.faforever.gw.model.GwCharacter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
public class User extends UsernamePasswordAuthenticationToken {

    private final long id;
    private Optional<GwCharacter> activeCharacter;

    public User(long id, Optional<GwCharacter> activeCharacter, Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.id = id;
        this.activeCharacter = activeCharacter;
    }

    public boolean hasPermission(String permission) {
        Collection<String> authorities = this.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return authorities.contains(permission);
    }
}
