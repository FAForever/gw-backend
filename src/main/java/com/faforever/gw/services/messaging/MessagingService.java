package com.faforever.gw.services.messaging;

import com.faforever.gw.websocket.WebsocketController;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.UUID;

@Slf4j
@Service
public class MessagingService {
    private final SimpMessagingTemplate template;

    @Inject
    public MessagingService(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void send(WebsocketMessage message) {
        WebsocketChannel channel = message.getChannel();

        switch(channel.getType()) {
            case PUBLIC:
                log.trace("Sending public message");
                template.convertAndSend(channel.getChannelName(), message);
                break;
            case FACTION:
                log.trace("Sending faction message (faction: {})", message.getFaction());
                template.convertAndSend(channel.toFactionString(message.getFaction()), message);
                break;
            case PRIVATE:
                message.getRecipients().forEach(user -> {
                    log.trace("Sending private message (user: {})", user.getName());
                    template.convertAndSendToUser(user.getName(), channel.getChannelName(), message);
                });
        }
    }

    public VariableMap createVariables(UUID requestId, UUID requestCharacter) {
        log.debug("-> set requestId: {}", requestId);
        log.debug("-> set requestCharacter: {}", requestCharacter);

        return Variables.createVariables()
                .putValue("requestId", requestId)
                .putValue("requestCharacter", requestCharacter);
    }
}
