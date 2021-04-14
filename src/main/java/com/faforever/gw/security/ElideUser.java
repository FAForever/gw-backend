package com.faforever.gw.security;

import org.springframework.security.core.Authentication;

import java.security.Principal;
import java.util.Optional;

public class ElideUser extends com.yahoo.elide.core.security.User {
  protected User fafUserDetails;

  public ElideUser(Principal principal) {
    super(principal);
    if (principal instanceof Authentication) {
      this.fafUserDetails = (User) ((Authentication) principal).getPrincipal();
    }
  }

  @Override
  public String getName() {
    return getFafUserDetails().map(details -> details.getActiveCharacter().getName()).orElse("");
  }

  @Override
  public boolean isInRole(String role) {
    return getFafUserDetails().map(details -> details.hasPermission(role)).orElse(false);
  }

  public Optional<User> getFafUserDetails() {
    return Optional.ofNullable(fafUserDetails);
  }
}
