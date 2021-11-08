package com.faforever.gw.security;

import com.faforever.gw.model.GwCharacter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
public class User implements Authentication {

    private final long id;
    private Optional<GwCharacter> activeCharacter;
    private List<String> roles;

    public User(long id, Optional<GwCharacter> activeCharacter, List<String> roles) {
        this.id = id;
        this.activeCharacter = activeCharacter;
    }

    public boolean hasPermission(String permission) {
        return roles.contains(permission);
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> (GrantedAuthority) () -> role).toList();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new UnsupportedOperationException("User is per definition authenticated");
    }
}
