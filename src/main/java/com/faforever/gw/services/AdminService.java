package com.faforever.gw.services;

import com.faforever.gw.bpmn.services.GwErrorType;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.SolarSystem;
import com.faforever.gw.model.repository.PlanetRepository;
import com.faforever.gw.model.repository.SolarSystemRepository;
import com.faforever.gw.security.GwUserRegistry;
import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.client.MessagingService;
import com.faforever.gw.services.messaging.client.incoming.LinkSolarSystemsRequestMessage;
import com.faforever.gw.services.messaging.client.incoming.SetPlanetFactionRequestMessage;
import com.faforever.gw.services.messaging.client.incoming.UnlinkSolarSystemsRequestMessage;
import com.faforever.gw.services.messaging.client.outgoing.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.UUID;

@Service
@Slf4j
public class AdminService {
    private final GwUserRegistry gwUserRegistry;
    private final MessagingService messagingService;
    private final SolarSystemRepository solarSystemRepository;
    private final PlanetRepository planetRepository;

    @Inject
    public AdminService(GwUserRegistry gwUserRegistry, MessagingService messagingService, SolarSystemRepository solarSystemRepository, PlanetRepository planetRepository) {
        this.gwUserRegistry = gwUserRegistry;
        this.messagingService = messagingService;
        this.solarSystemRepository = solarSystemRepository;
        this.planetRepository = planetRepository;
    }

    @Transactional
    public void onAddSolarSystemLink(LinkSolarSystemsRequestMessage message, User user) {
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
            messagingService.send(new SolarSystemsLinkedMessage(gwUserRegistry.getConnectedUsers(), from.getId(), to.getId()));
        }
    }

    @Transactional
    public void onRemoveSolarSystemLink(UnlinkSolarSystemsRequestMessage message, User user) {
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
            messagingService.send(new SolarSystemsUnlinkedMessage(gwUserRegistry.getConnectedUsers(), from.getId(), to.getId()));
        }
    }

    @Transactional
    public void onSetPlanetFaction(SetPlanetFactionRequestMessage message, User user) {
        Planet planet = planetRepository.findOne(message.getPlanetId());

        planet.setCurrentOwner(message.getNewOwner());
        sendAckToUser(user, message.getRequestId());
        messagingService.send(new PlanetOwnerChangedMessage(gwUserRegistry.getConnectedUsers(), planet.getId(), message.getNewOwner()));
    }

    private void sendErrorToUser(User user, UUID requestId, GwErrorType errorType) {
        messagingService.send(new ErrorMessage(user, requestId, errorType.getErrorCode(), errorType.getErrorMessage()));
    }

    private void sendAckToUser(User user, UUID requestId) {
        messagingService.send(new AckMessage(user, requestId));
    }
}
