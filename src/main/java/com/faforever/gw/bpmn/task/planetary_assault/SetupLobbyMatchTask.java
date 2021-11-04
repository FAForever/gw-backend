package com.faforever.gw.bpmn.task.planetary_assault;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.bpmn.services.PlanetaryAssaultService;
import com.faforever.gw.messaging.lobby.LobbyService;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.repository.BattleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SetupLobbyMatchTask implements JavaDelegate {
    private final PlanetaryAssaultService planetaryAssaultService;
    private final LobbyService lobbyService;
    private final BattleRepository battleRepository;

    @Override
    @Transactional(dontRollbackOn = BpmnError.class)
    public void execute(DelegateExecution execution) {
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution);
        log.debug("setup lobby match for battle {}", accessor.getBusinessKey());
        Battle battle = battleRepository.getOne(accessor.getBattleId());

        lobbyService.createGame(battle)
                .thenAccept(battle_ -> planetaryAssaultService.onMatchCreated(battle_, battle_.getFafGameId()))
                .exceptionally(e -> onMatchCreationFailed(battle, e));
    }

    private Void onMatchCreationFailed(Battle battle, Throwable e) {
        Throwable cause = e.getCause();
        //TODO: implement error handling

        return null;
    }
}
