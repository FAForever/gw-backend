package com.faforever.gw.services.messaging;

import java.io.Serializable;
import java.util.UUID;

public interface IncomingWebSocketMessage extends Serializable {
    UUID getRequestId();
}
