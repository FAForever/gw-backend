package com.faforever.gw.bpmn.accessor;

import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class InitiateAssaultEventMessage {
    private final RuntimeService runtimeService;

    public static final String MESSAGE_NAME = "Message_InitiateAssault";

    @Inject
    public InitiateAssaultEventMessage(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    public void createAndSend(GwCharacter initiator, Planet planet){
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("initiator", initiator);
        processVariables.put("planet", planet);
        processVariables.put("attackingFaction", initiator.getFaction());
        processVariables.put("defendingFaction", planet.getCurrentOwner());

        log.info("Create and send message {0} - {1}", processVariables);
        runtimeService.startProcessInstanceByMessage(MESSAGE_NAME, processVariables);
    }
}
