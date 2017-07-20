package com.faforever.gw.security;

import com.faforever.gw.model.GwCharacter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class User extends UsernamePasswordAuthenticationToken {

    private final long id;
  private GwCharacter activeCharacter;

    public User(long id, GwCharacter activeCharacter, Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
    super(principal, credentials, authorities);
    this.id = id;
    this.activeCharacter = activeCharacter;
  }

}
