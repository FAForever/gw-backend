package com.faforever.gw.messaging.client.outbound;

import com.faforever.gw.messaging.client.Audience;
import com.faforever.gw.messaging.client.ClientMessage;

public interface OutboundClientMessage extends ClientMessage {
    Audience getAudience();
}
