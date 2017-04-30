package com.faforever.gw.bpmn.message.planetary_assault;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.security.GwUserRegistry;
import com.faforever.gw.services.messaging.MessagingService;
import com.faforever.gw.services.messaging.outgoing.BattleParticipantLeftAssaultMessage;
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
    private final MessagingService messagingService;
    private final GwUserRegistry gwUserRegistry;

    @Inject
    public BattleParticipantLeftAssaultNotification(CharacterRepository characterRepository, BattleRepository battleRepository, MessagingService messagingService, GwUserRegistry gwUserRegistry) {
        this.characterRepository = characterRepository;
        this.battleRepository = battleRepository;
        this.messagingService = messagingService;
        this.gwUserRegistry = gwUserRegistry;
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
        messagingService.send(new BattleParticipantLeftAssaultMessage(gwUserRegistry.getConnectedUsers(), characterId, battleId, attackingFaction, defendingFaction));
    }
}
