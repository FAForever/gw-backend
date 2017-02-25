package com.faforever.gw.bpmn.services;

import com.faforever.gw.bpmn.message.UserErrorMessage;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.MessagingService;
import com.faforever.gw.websocket.incoming.InitiateAssaultMessage;
import com.faforever.gw.websocket.incoming.JoinAssaultMessage;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class PlanetaryAssaultService {
    private final ApplicationContext applicationContext;
    private final RuntimeService runtimeService;
    private final MessagingService messagingService;
    private final PlanetRepository planetRepository;

    public static final String INITIATE_ASSAULT_MESSAGE = "Message_InitiateAssault";
    public static final String PLAYER_JOINS_ASSAULT_MESSAGE = "Message_PlayerJoinsAssault";
    public static final String PLAYER_LEAVES_ASSAULT_MESSAGE = "Message_PlayerLeavesAssault";

    @Inject
    public PlanetaryAssaultService(ApplicationContext applicationContext, RuntimeService runtimeService, MessagingService messagingService, PlanetRepository planetRepository) {
        this.applicationContext = applicationContext;
        this.runtimeService = runtimeService;
        this.messagingService = messagingService;
        this.planetRepository = planetRepository;
    }

    @Transactional(dontRollbackOn = BpmnError.class)
    public void characterInitiatesAssault(InitiateAssaultMessage message, User user) {
        log.debug("characterInitiatesAssault");
        UUID battleUUID = UUID.fromString("56774dc6-c4d5-401c-9b2c-c7742318aea4");

        GwCharacter character = user.getActiveCharacter();
        Planet planet = planetRepository.getOne(message.getPlanetId());

        log.debug("Received InitiateAssaultEventMessage");
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("initiator", character.getId());
        processVariables.put("battle", battleUUID);
        processVariables.put("planet", planet.getId());
        processVariables.put("attackingFaction", character.getFaction());
        processVariables.put("defendingFaction", planet.getCurrentOwner());

        log.debug("-> added processVariables: {}", processVariables);
        runtimeService.startProcessInstanceByMessage(INITIATE_ASSAULT_MESSAGE, battleUUID.toString(), processVariables);
    }

    public void characterJoinsAssault(JoinAssaultMessage message, User user) {
        log.debug("characterJoinsAssault");

        UUID characterId = user.getActiveCharacter().getId();
        Map<String, Object> processVariables = ImmutableMap.of("lastJoinedCharacter", characterId);

        log.debug("-> set lastJoinedCharacter: {}", characterId);

        try {
            runtimeService.correlateMessage(PLAYER_JOINS_ASSAULT_MESSAGE, message.getBattleId().toString(), processVariables);
        } catch (MismatchingMessageCorrelationException e) {
            log.error("Battle {} is no active bpmn instance", message.getBattleId());
            sendErrorToUser(user, GwErrorType.BATTLE_INVALID);
        }
    }

    public void characterLeavesAssault(JoinAssaultMessage message, User user) {
        log.debug("characterLeavesAssault");

        UUID characterId = user.getActiveCharacter().getId();
        Map<String, Object> processVariables = ImmutableMap.of("lastLeftCharacter", characterId);

        log.debug("-> set lastLeftCharacter: {}", characterId);

        try {
            runtimeService.correlateMessage(PLAYER_LEAVES_ASSAULT_MESSAGE, message.getBattleId().toString(), processVariables);
        } catch (MismatchingMessageCorrelationException e) {
            log.error("Battle {} is no active bpmn instance", message.getBattleId());
            sendErrorToUser(user, GwErrorType.BATTLE_INVALID);
        }
    }

    private void sendErrorToUser(User user, GwErrorType errorType) {
        UserErrorMessage errorMessage = applicationContext.getBean(UserErrorMessage.class);
        errorMessage.setErrorCharacter(user.getActiveCharacter().getId());
        errorMessage.setErrorCode(errorType.getErrorCode());
        errorMessage.setErrorMessage(errorType.getErrorMessage());
        messagingService.send(errorMessage);
    }
}
