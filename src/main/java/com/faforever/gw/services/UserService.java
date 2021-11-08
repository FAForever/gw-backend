package com.faforever.gw.services;

import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.service.CharacterService;
import com.faforever.gw.security.GwUserRegistry;
import com.faforever.gw.security.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final CharacterService characterService;
    private final GwUserRegistry gwUserRegistry;

    public User getUserFromContext() {
        return (User) ( SecurityContextHolder.getContext().getAuthentication());
    }

    public Optional<User> getOnlineUserByFafId(long id) {
        return gwUserRegistry.getUser(id);
    }

    public GwCharacter getActiveCharacter(User user) {
        return characterService.getActiveCharacterByFafId(user.getId())
                .orElseThrow(() -> new IllegalStateException("No active character found for user: " + user));
    }
}
