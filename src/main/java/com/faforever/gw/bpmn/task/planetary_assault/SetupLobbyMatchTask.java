package com.faforever.gw.bpmn.task.planetary_assault;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.messaging.lobby.LobbyService;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.repository.BattleRepository;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static com.google.common.collect.ImmutableList.toImmutableList;

@Slf4j
@Component
public class SetupLobbyMatchTask implements JavaDelegate {
    private final LobbyService lobbyService;
    private final BattleRepository battleRepository;

    @Inject
    public SetupLobbyMatchTask(LobbyService lobbyService, BattleRepository battleRepository) {
        this.lobbyService = lobbyService;
        this.battleRepository = battleRepository;
    }

    @Override
    @Transactional(dontRollbackOn = BpmnError.class)
    public void execute(DelegateExecution execution) {
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution);
        log.debug("setup lobby match for battle {}", accessor.getBusinessKey());
        Battle battle = battleRepository.getOne(accessor.getBattleId());

        ImmutableList<Long> participantFafIds = battle.getParticipants().stream()
                .map(participant -> participant.getCharacter().getFafId())
                .collect(toImmutableList());

        lobbyService.createGame(battle.getId(), participantFafIds);
    }
}
