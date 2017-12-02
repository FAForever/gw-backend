package com.faforever.gw.bpmn.message.planetary_assault;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.outbound.BattleParticipantLeftAssaultMessage;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
public class BattleParticipantLeftAssaultNotification implements JavaDelegate {
    private final CharacterRepository characterRepository;
    private final BattleRepository battleRepository;
    private final ClientMessagingService clientMessagingService;

    @Inject
    public BattleParticipantLeftAssaultNotification(CharacterRepository characterRepository, BattleRepository battleRepository, ClientMessagingService clientMessagingService) {
        this.characterRepository = characterRepository;
        this.battleRepository = battleRepository;
        this.clientMessagingService = clientMessagingService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution);

        GwCharacter character = characterRepository.getOne(accessor.getRequestCharacter());
        val characterId = character.getId();
        val battleId = accessor.getBattleId();
        val attackingFaction = character.getFaction();
        val defendingFaction = battleRepository.getOne(battleId).getDefendingFaction();

        log.debug("Sending BattleParticipantLeftAssaultMessage (characterId: {}, battleId: {}, characterFaction: {}, defendingFaction: {})", characterId, battleId, attackingFaction, defendingFaction);
        clientMessagingService.sendToPublic(new BattleParticipantLeftAssaultMessage(characterId, battleId, attackingFaction, defendingFaction));
    }
}
