package com.faforever.gw.services;

import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.service.CharacterService;
import com.faforever.gw.security.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final CharacterService characterService;

    public UserService(CharacterService characterService) {
        this.characterService = characterService;
    }

    public User getUserFromContext() {
        return (User) ((OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication()).getUserAuthentication();
    }

    public GwCharacter getActiveCharacter(User user) {
        return characterService.getActiveCharacterByFafId(user.getId());
    }
}
