package com.faforever.gw.bpmn.message;

import com.faforever.gw.model.Battle;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Planet;
import com.faforever.gw.websocket.WebsocketController;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Getter
@Component
public class PlanetUnderAssaultBroadcastMessage implements JavaDelegate {
    @Getter(AccessLevel.NONE)
    private final WebsocketController websocketController;

    private UUID planetId;
    private UUID battleId;
    private Faction attackingFaction;
    private Faction defendingFaction;

    public PlanetUnderAssaultBroadcastMessage(WebsocketController websocketController) {
        this.websocketController = websocketController;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Planet planet = (Planet)execution.getVariable("planet");
        GwCharacter character = (GwCharacter)execution.getVariable("initiator");
        Battle battle = (Battle)execution.getVariable("battle");

        planetId = planet.getId();
        battleId = battle.getId();
        attackingFaction = character.getFaction();
        defendingFaction = planet.getCurrentOwner();

        websocketController.broadcast("/planets/attacked", this);
    }

}
