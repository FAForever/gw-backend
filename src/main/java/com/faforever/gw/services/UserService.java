package com.faforever.gw.services;

import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.service.CharacterService;
import com.faforever.gw.security.GwUserRegistry;
import com.faforever.gw.security.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final CharacterService characterService;
    private final GwUserRegistry gwUserRegistry;

    public UserService(CharacterService characterService, GwUserRegistry gwUserRegistry) {
        this.characterService = characterService;
        this.gwUserRegistry = gwUserRegistry;
    }

    public User getUserFromContext() {
        return (User) ((OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication()).getUserAuthentication();
    }

    public Optional<User> getOnlineUserByFafId(long id) {
        return gwUserRegistry.getUser(id);
    }

    public GwCharacter getActiveCharacter(User user) {
        return characterService.getActiveCharacterByFafId(user.getId());
    }
}
