package com.faforever.gw.model;

import com.faforever.gw.bpmn.services.GwErrorService;
import com.faforever.gw.bpmn.services.GwErrorType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.EnumSet;

@Component
public class ValidationHelper {
    private static final EnumSet<BattleStatus> OCCUPIED_BATTLE_STATUSSES = EnumSet.of(BattleStatus.INITIATED, BattleStatus.RUNNING);
    private final GwErrorService gwErrorService;

    @Inject
    public ValidationHelper(GwErrorService gwErrorService) {
        this.gwErrorService = gwErrorService;
    }

    public void validateCharacterInBattle(GwCharacter character, Battle battle, boolean expect) {
        if (battle.getParticipants().stream()
                .anyMatch(battleParticipant -> battleParticipant.getCharacter() == character) != expect) {
            throw gwErrorService.getBpmnErrorOf(GwErrorType.CHARACTER_ALREADY_IN_BATTLE);
        }
    }

    public void validateCharacterFreeForGame(GwCharacter character) {
        if (character.getBattleParticipantList().stream()
                .filter(battleParticipant -> OCCUPIED_BATTLE_STATUSSES.contains(battleParticipant.getBattle().getStatus()))
                .count() > 0) {
            throw gwErrorService.getBpmnErrorOf(GwErrorType.CHARACTER_ALREADY_IN_BATTLE);
        }
    }

    public void validateOpenSlotForCharacter(Battle battle, BattleRole battleRole) {
        if (battleRole == null) {
            throw gwErrorService.getBpmnErrorOf(GwErrorType.NO_SLOTS_FOR_FACTION);
        }

        long characterCount = battle.getParticipants().stream()
                .filter(battleParticipant -> battleParticipant.getRole() == battleRole)
                .count();

        if (characterCount >= (battle.getPlanet().getMap().getTotalSlots() / 2)) {
            throw gwErrorService.getBpmnErrorOf(GwErrorType.NO_SLOTS_FOR_FACTION);
        }
    }

    public void validateAssaultOnPlanet(GwCharacter character, Planet planet) {
        if (character.getFaction() == planet.getCurrentOwner()) {
            throw gwErrorService.getBpmnErrorOf(GwErrorType.PLANET_OWNED_BY_CHARACTERS_FACTION);
        }

        if (false) { // TODO: Implement check for protected planets
            throw gwErrorService.getBpmnErrorOf(GwErrorType.PLANET_PROTECTED_FROM_ASSAULT);
        }

        if (false) {// TODO: Implement check whether character's faction can reach planet
            throw gwErrorService.getBpmnErrorOf(GwErrorType.PLANET_NOT_IN_REACH);
        }
    }
}
