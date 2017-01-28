package com.faforever.gw.websocket;

import com.faforever.gw.model.Battle;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import com.faforever.gw.websocket.incoming.InitiateAssaultMessage;
import com.faforever.gw.websocket.incoming.JoinAssaultMessage;
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
    private final BattleRepository battleRepository;

    @Inject
    public WebsocketController(RuntimeService runtimeService, CharacterRepository characterRepository, PlanetRepository planetRepository, BattleRepository battleRepository) {
        this.runtimeService = runtimeService;
        this.characterRepository = characterRepository;
        this.planetRepository = planetRepository;
        this.battleRepository = battleRepository;
    }

    @MessageMapping("/initiateAssault")
    @Transactional
    public void initiateAssault(InitiateAssaultMessage message) throws Exception {
        // TODO: Identify User via Principal
        //UUID battleUUID = UUID.randomUUID();
        UUID battleUUID = UUID.fromString("56774dc6-c4d5-401c-9b2c-c7742318aea4");

        GwCharacter character = characterRepository.getOne(UUID.fromString("a81dba16-e35c-11e6-bf01-fe55135034f3"));
        Planet planet = planetRepository.getOne(message.getPlanetId());

        Map<String, Object> processVariables = ImmutableMap.of("character", character,
                "planet", planet,
                "attackingFaction", character.getFaction(),
                "defendingFaction", Faction.CYBRAN);

        runtimeService.startProcessInstanceByMessage("Message_InitiateAssault", battleUUID.toString(), processVariables);

//        return new Greeting("Hello, " + message.getName() + "!");
    }

    @MessageMapping("/joinAssault")
    @Transactional
    public void joinAssault(JoinAssaultMessage message) throws Exception {
        // TODO: Identify User via Principal
        GwCharacter character = characterRepository.getOne(UUID.fromString("a2e67506-e4e2-11e6-bf01-fe55135034f3"));
        Map<String, Object> processVariables = ImmutableMap.of("character", character);

        runtimeService.correlateMessage("Message_PlayerJoinsAssault", message.getBattleId().toString(), processVariables);
    }
}
