package com.faforever.gw.bpmn.accessors;

import com.faforever.gw.model.Faction;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import java.util.List;
import java.util.UUID;

public class CharacterCreationAccessor extends UserInteractionProcessAccessor {
    private CharacterCreationAccessor(DelegateExecution processContext) {
        super(processContext);
    }

    public static CharacterCreationAccessor of(DelegateExecution processContext) {
        return new CharacterCreationAccessor(processContext);
    }

    public UUID getNewCharacterId() {
        return UUID.fromString((String) get("newCharacterId"));
    }

    public CharacterCreationAccessor setNewCharacterId(UUID newCharacterId) {
        set("newCharacterId", newCharacterId.toString());
        return this;
    }

    public Faction getRequestedFaction() {
        return (Faction) get("requestedFaction");
    }

    public List<String> getProposedNamesList() {
        return (List<String>) get("proposedNamesList");
    }

    public CharacterCreationAccessor setProposedNamesList(List<String> proposedNamesList) {
        set("proposedNamesList", proposedNamesList);
        return this;
    }

    public String getSelectedName() {
        return (String) get("selectedName");
    }

    public CharacterCreationAccessor setSelectedName(String characterName) {
        set("selectedName", characterName);
        return this;
    }

    public CharacterCreationAccessor setHasActiveCharacter(boolean hasActiveCharacter) {
        set("hasActiveCharacter", hasActiveCharacter);
        return this;
    }

    public CharacterCreationAccessor setHasDeadCharacter(boolean hasDeadCharacter) {
        set("hasDeadCharacter", hasDeadCharacter);
        return this;
    }

    public CharacterCreationAccessor setFactionMatches(boolean factionMatches) {
        set("factionMatches", factionMatches);
        return this;
    }
}
