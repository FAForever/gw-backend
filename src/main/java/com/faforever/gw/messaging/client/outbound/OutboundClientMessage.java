package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import com.faforever.gw.messaging.client.ClientMessage;

public abstract class OutboundClientMessage extends ClientMessage {
    public abstract Audience getAudience();
}
