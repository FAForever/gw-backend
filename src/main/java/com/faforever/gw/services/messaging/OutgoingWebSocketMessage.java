package com.faforever.gw.services.messaging;

import com.faforever.gw.model.Faction;
import com.faforever.gw.security.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Collection;

public interface OutgoingWebSocketMessage extends Serializable {
    @JsonIgnore
    MessageType getAction();
    @JsonIgnore
    default Faction getFaction() { return null; }
    @JsonIgnore
    default Collection<User> getRecipients() { return null; }
}
