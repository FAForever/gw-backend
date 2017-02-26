package com.faforever.gw.bpmn.message;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.services.messaging.MessagingService;
import com.faforever.gw.services.messaging.WebsocketChannel;
import com.faforever.gw.services.messaging.WebsocketMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.UUID;

@Slf4j
@Getter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BattleUpdateWaitingProgressMessage implements JavaDelegate, WebsocketMessage {
    @Getter(AccessLevel.NONE)
    private final MessagingService messagingService;

    private UUID battleId;
    private Double waitingProgress;

    @Inject
    public BattleUpdateWaitingProgressMessage(MessagingService messagingService, CharacterRepository characterRepository) {
        this.messagingService = messagingService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution.getVariables());

        battleId = accessor.getBattleId();
        waitingProgress = accessor.getWaitingProgress();

        log.debug("Sending BattleUpdateWaitingProgressMessage (battleId: {}, waitingProgress: {}", battleId, waitingProgress);
        messagingService.send(this);
    }

    @Override
    public WebsocketChannel getChannel() {
        return WebsocketChannel.BATTLE_WAITING_PROGRESS;
    }
}
