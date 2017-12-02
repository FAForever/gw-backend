package com.faforever.gw.security;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class GwAuthentication extends UsernamePasswordAuthenticationToken {
    private final long userId;
    private final String userName;

    public GwAuthentication(long userId, String userName, Collection<? extends GrantedAuthority> authorities) {
        super(userId, "n/a", authorities);
        this.userId = userId;
        this.userName = userName;
    }
}
