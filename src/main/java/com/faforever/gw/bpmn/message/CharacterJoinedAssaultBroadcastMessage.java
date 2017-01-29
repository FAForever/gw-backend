package com.faforever.gw.bpmn.message;

import com.faforever.gw.model.Battle;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Planet;
import com.faforever.gw.websocket.WebsocketController;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Getter
@Component
public class CharacterJoinedAssaultBroadcastMessage implements JavaDelegate {
    @Getter(AccessLevel.NONE)
    private final WebsocketController websocketController;

    private UUID characterId;
    private UUID battleId;
    private Faction characterFaction;

    public CharacterJoinedAssaultBroadcastMessage(WebsocketController websocketController) {
        this.websocketController = websocketController;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        GwCharacter character = (GwCharacter)execution.getVariable("character");
        Battle battle = (Battle)execution.getVariable("battle");

        characterId = character.getId();
        battleId = battle.getId();
        characterFaction = character.getFaction();

        websocketController.broadcast("/battles/character_joined", this);
    }

}
