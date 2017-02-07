package com.faforever.gw.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class User extends UsernamePasswordAuthenticationToken {

  private final Long id;

  public User(Long id, Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
    super(principal, credentials, authorities);
    this.id = id;
  }

  public static User fromJwtToken(String stringToken) {
    try {
      Jwt token = JwtHelper.decodeAndVerify(stringToken, new MacSigner("secret"));
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode data = objectMapper.readTree(token.getClaims());

      List<String> authorities = objectMapper.readerFor(new TypeReference<List<String>>(){}).readValue(data.get("authorities"));
      List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();

      authorities.forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role)));

      return new User(data.get("user_id").asLong(), data.get("user_name").asText(), stringToken, grantedAuthorities);
    }
    catch(Exception e) {
      throw new RuntimeException("user not authorized");
    }
  }
}
