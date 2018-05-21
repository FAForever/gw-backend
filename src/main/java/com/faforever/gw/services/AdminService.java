package com.faforever.gw.services;

import com.faforever.gw.bpmn.services.GwErrorType;
import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.inbound.DebugMessage;
import com.faforever.gw.messaging.client.inbound.LinkSolarSystemsRequestMessage;
import com.faforever.gw.messaging.client.inbound.SetPlanetFactionRequestMessage;
import com.faforever.gw.messaging.client.inbound.UnlinkSolarSystemsRequestMessage;
import com.faforever.gw.messaging.client.outbound.*;
import com.faforever.gw.messaging.lobby.LobbyService;
import com.faforever.gw.messaging.lobby.outbound.CreateMatchRequest;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.SolarSystem;
import com.faforever.gw.model.repository.PlanetRepository;
import com.faforever.gw.model.repository.SolarSystemRepository;
import com.faforever.gw.security.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AdminService {
    private final ClientMessagingService clientMessagingService;
    private final SolarSystemRepository solarSystemRepository;
    private final PlanetRepository planetRepository;
    private final UserService userService;
    private final LobbyService lobbyService;

    @Inject
    public AdminService(ClientMessagingService clientMessagingService, SolarSystemRepository solarSystemRepository, PlanetRepository planetRepository, UserService userService, LobbyService lobbyService) {
        this.clientMessagingService = clientMessagingService;
        this.solarSystemRepository = solarSystemRepository;
        this.planetRepository = planetRepository;
        this.userService = userService;
        this.lobbyService = lobbyService;
    }

    @EventListener
    @Transactional
    public void onAddSolarSystemLink(LinkSolarSystemsRequestMessage message) {
        User user = userService.getUserFromContext();
        log.debug("onAddSolarSystemLink by user {}", user.getId());

        SolarSystem from = solarSystemRepository.findOne(message.getSolarSystemFrom());
        SolarSystem to = solarSystemRepository.findOne(message.getSolarSystemTo());

        if (from.getConnectedSystems().contains(to)) {
            log.error("Adding solar system link failed from {} to {} - already linked", from, to);
            sendErrorToUser(user, message.getRequestId(), GwErrorType.ALREADY_LINKED);
        } else {
            log.info("Add solar system link from {} to {}", from, to);
            from.getConnectedSystems().add(to);
            to.getConnectedSystems().add(from);
            sendAckToUser(user, message.getRequestId());
            clientMessagingService.sendToPublic(new SolarSystemsLinkedMessage(from.getId(), to.getId()));
        }
    }

    @EventListener
    @Transactional
    public void onRemoveSolarSystemLink(UnlinkSolarSystemsRequestMessage message) {
        User user = userService.getUserFromContext();
        log.debug("onRemoveSolarSystemLink by user {}", user.getId());
        SolarSystem from = solarSystemRepository.findOne(message.getSolarSystemFrom());
        SolarSystem to = solarSystemRepository.findOne(message.getSolarSystemTo());

        if (!from.getConnectedSystems().contains(to)) {
            log.error("Removing solar system link failed from {} to {} - not linked", from, to);
            sendErrorToUser(user, message.getRequestId(), GwErrorType.NOT_LINKED);
        } else {
            log.info("Removing solar system link from {} to {}", from, to);
            from.getConnectedSystems().remove(to);
            to.getConnectedSystems().remove(from);
            sendAckToUser(user, message.getRequestId());
            clientMessagingService.sendToPublic(new SolarSystemsUnlinkedMessage(from.getId(), to.getId()));
        }
    }

    @EventListener
    @Transactional
    public void onSetPlanetFaction(SetPlanetFactionRequestMessage message) {
        User user = userService.getUserFromContext();
        log.debug("onSetPlanetFaction by user {}", user.getId());
        Planet planet = planetRepository.findOne(message.getPlanetId());

        if (planet == null) {
            sendErrorToUser(user, message.getRequestId(), GwErrorType.PLANET_DOES_NOT_EXIST);
            return;
        }

        planet.setCurrentOwner(message.getNewOwner());
        sendAckToUser(user, message.getRequestId());
        clientMessagingService.sendToPublic(new PlanetOwnerChangedMessage(planet.getId(), message.getNewOwner()));
    }

    @EventListener
    public void onDebugMessage(DebugMessage message) {
        switch (message.getAction()) {
            case "dummyHostGame":
                List<CreateMatchRequest.Participant> participants = new ArrayList<>();

                participants.add(
                        new CreateMatchRequest.Participant()
                                .setId(1)
                                .setName("UEF Alpha")
                                .setFaction(Faction.UEF)
                                .setTeam(1)
                );

                participants.add(
                        new CreateMatchRequest.Participant()
                                .setId(3)
                                .setName("Cybran Charlie")
                                .setFaction(Faction.CYBRAN)
                                .setTeam(2)
                );

                CreateMatchRequest createMatchRequest = new CreateMatchRequest()
                        .setTitle("Galactic War battle demo")
                        .setFeaturedMod("fafdevelop") // TODO: set to faf-gw
                        .setMap(1)
                        .setParticipants(participants);

                lobbyService.createGame(createMatchRequest)
                        .thenAccept(matchCreatedMessage -> log.debug("Debug match created"))
                        .exceptionally(throwable -> {
                            log.error("Debug match creation failed");
                            return null;
                        });
        }
    }

    private void sendErrorToUser(User user, UUID requestId, GwErrorType errorType) {
        clientMessagingService.sendToUser(new ErrorMessage(requestId, errorType.getErrorCode(), errorType.getErrorMessage()), user);
    }

    private void sendAckToUser(User user, UUID requestId) {
        clientMessagingService.sendToUser(new AckMessage(requestId), user);
    }
}
