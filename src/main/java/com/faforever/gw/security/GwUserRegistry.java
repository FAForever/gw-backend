package com.faforever.gw.security;

import com.faforever.gw.model.GwCharacter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.of;

@Component
public class GwUserRegistry {
    private final BiMap<User, GwCharacter> userCharacterMapping;

    public GwUserRegistry() {
        userCharacterMapping = HashBiMap.create();
    }

    public void addConnection(User user) {
        userCharacterMapping.forcePut(user, user.getActiveCharacter());
    }

    public void removeConnection(User user) {
        userCharacterMapping.remove(user, user.getActiveCharacter());
    }

    public GwCharacter getCharacter(User user) {
        return userCharacterMapping.get(user);
    }

    public Optional<User> getUser(UUID characterId) {
        BiMap<GwCharacter, User> inverse = userCharacterMapping.inverse();

        return inverse.keySet().stream()
                .filter(gwCharacter -> Objects.equals(gwCharacter.getId(), characterId))
                .findFirst()
                .map(inverse::get);
    }

    public Optional<User> getUser(GwCharacter character) {
        return of(userCharacterMapping.inverse().get(character));
    }

    public Collection<User> getConnectedUsers() {
        return userCharacterMapping.keySet();
    }
}
