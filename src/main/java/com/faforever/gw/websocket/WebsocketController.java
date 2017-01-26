package com.faforever.gw.websocket;

import com.faforever.gw.model.Faction;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import com.faforever.gw.websocket.incoming.InitiateAttackMessage;
import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Map;
import java.util.UUID;

@Controller
public class WebsocketController {
    private final RuntimeService runtimeService;
    private final CharacterRepository characterRepository;
    private final PlanetRepository planetRepository;

    @Inject
    public WebsocketController(RuntimeService runtimeService, CharacterRepository characterRepository, PlanetRepository planetRepository) {
        this.runtimeService = runtimeService;
        this.characterRepository = characterRepository;
        this.planetRepository = planetRepository;
    }

    @MessageMapping("/initiateAttack")
    @Transactional
    public void initiateAttack(InitiateAttackMessage message) throws Exception {
        // TODO: Identify User via Principal
        UUID battleUUID = UUID.randomUUID();

        GwCharacter character = characterRepository.getOne(UUID.fromString("a81dba16-e35c-11e6-bf01-fe55135034f3"));
        Planet planet = planetRepository.getOne(message.getPlanetId());

        Map<String, Object> processVariables = ImmutableMap.of("character", character,
                "planet", planet,
                "attackingFaction", character.getFaction(),
                "defendingFaction", Faction.CYBRAN);

        runtimeService.startProcessInstanceByMessage("Message_InitiateAttack", battleUUID.toString(), processVariables);

//        return new Greeting("Hello, " + message.getName() + "!");
    }
}
