package com.faforever.gw.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class User extends org.springframework.security.core.userdetails.User {

  private final Integer id;
  private String password;

  public User(Integer id, String username, String password, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
    super(username, password, true, true, true, accountNonLocked, authorities);
    this.id = id;
  }


  public String getPassword() {
    return password;
  }
}
