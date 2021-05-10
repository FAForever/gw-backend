package com.faforever.gw.bpmn.task.planetary_assault;

import com.faforever.gw.bpmn.accessors.PlanetaryAssaultAccessor;
import com.faforever.gw.bpmn.services.PlanetaryAssaultService;
import com.faforever.gw.messaging.lobby.LobbyService;
import com.faforever.gw.messaging.lobby.inbound.ServerErrorException;
import com.faforever.gw.messaging.lobby.outbound.CreateMatchRequest;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.BattleParticipant;
import com.faforever.gw.model.BattleRole;
import com.faforever.gw.model.repository.BattleRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class SetupLobbyMatchTask implements JavaDelegate {
    private final PlanetaryAssaultService planetaryAssaultService;
    private final LobbyService lobbyService;
    private final BattleRepository battleRepository;

    @Inject
    public SetupLobbyMatchTask(PlanetaryAssaultService planetaryAssaultService, LobbyService lobbyService, BattleRepository battleRepository) {
        this.planetaryAssaultService = planetaryAssaultService;
        this.lobbyService = lobbyService;
        this.battleRepository = battleRepository;
    }

    @Override
    @Transactional(dontRollbackOn = BpmnError.class)
    public void execute(DelegateExecution execution) {
        PlanetaryAssaultAccessor accessor = PlanetaryAssaultAccessor.of(execution);
        log.debug("setup lobby match for battle {}", accessor.getBusinessKey());
        Battle battle = battleRepository.getOne(accessor.getBattleId());

        List<CreateMatchRequest.Participant> participants = new ArrayList<>();

        for (BattleParticipant battleParticipant : battle.getParticipants()) {
            participants.add(
                    new CreateMatchRequest.Participant(
                            battleParticipant.getCharacter().getFafId(),
                            battleParticipant.getFaction(),
                            -1, // TODO: Resolve this
                            battleParticipant.getRole() == BattleRole.ATTACKER ? 1 : 2,
                            battleParticipant.getCharacter().getName()
                    )
            );
        }

        CreateMatchRequest createMatchRequest = new CreateMatchRequest(
                "Galactic War battle " + accessor.getBattleId(),
                battle.getPlanet().getMap().getFafMapVersion(),
                "fafdevelop", // TODO: set to faf-gw
                participants
        );

        lobbyService.createGame(createMatchRequest)
                .thenAccept(message -> planetaryAssaultService.onMatchCreated(battle, message.getGameId()))
                .exceptionally(this::onMatchCreationFailed);
    }

    private Void onMatchCreationFailed(Throwable e) {
        Throwable cause = e.getCause();
        if (!(cause instanceof ServerErrorException)) {
            log.error("Unknown error received (expected ServerErrorException", cause);
            return null;
        }

        ServerErrorException error = (ServerErrorException) cause;
        //TODO: implement error handling

        return null;
    }
}
