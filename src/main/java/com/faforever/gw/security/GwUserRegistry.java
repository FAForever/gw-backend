package com.faforever.gw.security;

import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.CharacterRepository;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GwUserRegistry {
    private final BiMap<User, Long> userCharacterMapping = HashBiMap.create();
    private final CharacterRepository characterRepository;

    public void addConnection(User user) {
        userCharacterMapping.forcePut(user, user.getId());
    }

    public void removeConnection(User user) {
        userCharacterMapping.remove(user, user.getId());
    }

    public Optional<User> getUser(UUID gwCharacterId) {
        return characterRepository.findById(gwCharacterId)
                .flatMap(character -> getUser(character.getFafId()));
    }

    public Optional<User> getUser(long fafUserId) {
        BiMap<Long, User> inverse = userCharacterMapping.inverse();

        return inverse.keySet().stream()
                .filter(aLong -> Objects.equals(aLong, fafUserId))
                .findFirst()
                .map(inverse::get);
    }


    public Collection<User> getConnectedUsers() {
        return userCharacterMapping.keySet();
    }
}
