package com.faforever.gw.services.messaging.client;

import com.faforever.gw.model.Faction;
import com.faforever.gw.security.User;
import lombok.val;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collection;

public abstract class AbstractOutgoingWebSocketMessage implements OutgoingWebSocketMessage {
    private Faction faction;
    private Collection<User> recipients;

    /**
     * Default constructor to send a message to a group of users
     *
     * @param recipients: mandatory list of recipients
     * @param faction:    mandatory if channel is faction based, can be null otherwise
     */
    public AbstractOutgoingWebSocketMessage(@NotNull Collection<User> recipients, Faction faction) {
        this.faction = faction;
        this.recipients = recipients;
    }

    /**
     * Convenience constructor to send a message to a single user
     *
     * @param user: user the message will be sent to
     */
    public AbstractOutgoingWebSocketMessage(@NotNull User user) {
        recipients = Arrays.asList(user);

        val activeCharacter = user.getActiveCharacter();
        faction = activeCharacter == null ? null : activeCharacter.getFaction();
    }

    @Override
    public Faction getFaction() {
        return faction;
    }

    @Override
    public Collection<User> getRecipients() {
        return recipients;
    }
}
