package com.faforever.gw.services.messaging;

import com.faforever.gw.websocket.WebsocketController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Slf4j
@Service
public class MessagingService {
    private final WebsocketController websocketController;

    @Inject
    public MessagingService(WebsocketController websocketController) {
        this.websocketController = websocketController;
    }

    public void send(WebsocketMessage message) {
        WebsocketChannel channel = message.getChannel();

        switch(channel.getType()) {
            case PUBLIC:
                log.trace("Sending public message");
                websocketController.send(channel.getChannelName(), message);
                break;
            case FACTION:
                log.trace("Sending faction message (faction: {})", message.getFaction());
                websocketController.send(channel.toFactionString(message.getFaction()), message);
                break;
            case PRIVATE:
                message.getRecipients().forEach(user -> {
                    log.trace("Sending private message (user: {})", user.getName());
                    websocketController.send(channel.toUserString(user), message);
                });
        }
    }
}
