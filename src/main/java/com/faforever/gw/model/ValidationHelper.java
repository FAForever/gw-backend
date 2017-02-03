package com.faforever.gw.model;

import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.MapRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.EnumSet;

@Component
public class ValidationHelper {
    private static final EnumSet<BattleStatus> OCCUPIED_BATTLE_STATUSSES = EnumSet.of(BattleStatus.INITIATED, BattleStatus.RUNNING);

    public void validateCharacterInBattle(GwCharacter character, Battle battle, boolean expect){
        if(character.getBattleParticipantList().stream()
                .anyMatch(battleParticipant -> battleParticipant.getCharacter() == character) != expect){
            throw GwError.CHARACTER_ALREADY_IN_BATTLE.asBpmnError();
        }
    }

    public void validateCharacterFreeForGame(GwCharacter character) {
        if(character.getBattleParticipantList().stream()
                .filter(battleParticipant -> OCCUPIED_BATTLE_STATUSSES.contains(battleParticipant.getBattle().getStatus()))
                .count() > 0) {
            throw GwError.CHARACTER_ALREADY_IN_BATTLE.asBpmnError();
        }
    }

    public void validateOpenSlotForCharacter(GwCharacter character, Battle battle, BattleRole battleRole){
        if(battleRole == null) {
            throw GwError.NO_SLOTS_FOR_FACTION.asBpmnError();
        }

        long characterCount = battle.getParticipants().stream()
                .filter(battleParticipant -> battleParticipant.getRole() == battleRole)
                .count();

        if(characterCount >= (battle.getPlanet().getMap().getTotalSlots() / 2)){
            throw GwError.NO_SLOTS_FOR_FACTION.asBpmnError();
        }
    }

    public void validateAssaultOnPlanet(GwCharacter character, Planet planet) {
        if( character.getFaction() == planet.getCurrentOwner()) {
            throw GwError.PLANET_OWNED_BY_CHARACTERS_FACTION.asBpmnError();
        }

        if(false) { // TODO: Implement check for protected planets
            throw GwError.PLANET_PROTECTED_FROM_ASSAULT.asBpmnError();
        }

        if(false) {// TODO: Implement check whether character's faction can reach planet
            throw GwError.PLANET_NOT_IN_REACH.asBpmnError();
        }
    }
}
