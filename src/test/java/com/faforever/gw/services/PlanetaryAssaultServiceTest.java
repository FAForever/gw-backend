package com.faforever.gw.services;

import com.faforever.gw.bpmn.message.generic.UserErrorMessage;
import com.faforever.gw.bpmn.services.PlanetaryAssaultService;
import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.inbound.InitiateAssaultMessage;
import com.faforever.gw.messaging.client.inbound.JoinAssaultMessage;
import com.faforever.gw.messaging.client.inbound.LeaveAssaultMessage;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import com.faforever.gw.security.User;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlanetaryAssaultServiceTest {
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private ProcessEngine processEngine;
    @Mock
    private RuntimeService runtimeService;
    @Mock
    private ClientMessagingService clientMessagingService;
    @Mock
    private PlanetRepository planetRepository;
    @Mock
    private UserService userService;
    @Mock
    private BattleRepository battleRepository;

    @Mock
    private User user;
    @Mock
    private GwCharacter character;
    @Mock
    private Planet planet;
    @Mock
    private Battle battle;

    private VariableMap map;
    private PlanetaryAssaultService service;

    @BeforeEach
    public void setUp() throws Exception {
        lenient().when(userService.getUserFromContext()).thenReturn(user);
        lenient().when(planetRepository.findById(any(UUID.class))).thenReturn(Optional.of(planet));

        lenient().when(battleRepository.findOneByFafGameId(anyLong())).thenReturn(Optional.of(battle));
        lenient().when(battle.getId()).thenReturn(UUID.randomUUID());

        lenient().when(user.getId()).thenReturn(5L);
        lenient().when(user.getActiveCharacter()).thenReturn(Optional.of(character));

        map = Variables.createVariables();
        lenient().when(clientMessagingService.createVariables(anyLong(), any(), any())).thenReturn(map);

        service = new PlanetaryAssaultService(processEngine, runtimeService, clientMessagingService, battleRepository, planetRepository, userService);
    }

    @Test
    public void onCharacterInitiatesAssaultTest() throws Exception {
        InitiateAssaultMessage message = new InitiateAssaultMessage(
                UUID.fromString("22222222-2222-2222-2222-222222222222")
        );


        when(character.getId()).thenReturn(UUID.fromString("33333333-3333-3333-3333-333333333333"));
        when(character.getFaction()).thenReturn(Faction.UEF);

        when(planet.getId()).thenReturn(UUID.fromString("44444444-4444-4444-4444-444444444444"));
        when(planet.getCurrentOwner()).thenReturn(Faction.CYBRAN);


        service.onCharacterInitiatesAssault(message);

        verify(clientMessagingService).createVariables(user.getId(), message.getRequestId(), character.getId());
        verify(runtimeService).startProcessInstanceByMessage(eq(PlanetaryAssaultService.INITIATE_ASSAULT_MESSAGE), anyString(), any());

        assertEquals(map.get("planet"), planet.getId());
        assertEquals(map.get("attackingFaction"), character.getFaction());
        assertEquals(map.get("defendingFaction"), planet.getCurrentOwner());
        assertEquals(map.get("attackerCount"), 1);
        assertEquals(map.get("defenderCount"), 0);
        assertEquals(map.get("gameFull"), false);
        assertEquals(map.get("waitingProgress"), 0.0d);
    }

    @Test
    public void onCharacterJoinsAssault_Success() throws Exception {
        JoinAssaultMessage message = new JoinAssaultMessage(
                UUID.fromString("22222222-2222-2222-2222-222222222222")
        );

        service.onCharacterJoinsAssault(message);

        verify(clientMessagingService).createVariables(user.getId(), message.getRequestId(), character.getId());
        verify(runtimeService).correlateMessage(eq(PlanetaryAssaultService.PLAYER_JOINS_ASSAULT_MESSAGE), anyString(), any());
    }

    @Test
    public void onCharacterJoinsAssault_Fail() throws Exception {
        JoinAssaultMessage message = new JoinAssaultMessage(
                UUID.fromString("22222222-2222-2222-2222-222222222222")
        );

        doThrow(MismatchingMessageCorrelationException.class).when(runtimeService).correlateMessage(anyString(), anyString(), any());

        service.onCharacterJoinsAssault(message);

        verify(clientMessagingService).createVariables(user.getId(), message.getRequestId(), character.getId());
        verify(runtimeService).correlateMessage(eq(PlanetaryAssaultService.PLAYER_JOINS_ASSAULT_MESSAGE), anyString(), any());
        verify(clientMessagingService).sendToUser(any(), any(User.class));
    }

    @Test
    public void onCharacterLeaveAssault_Success() throws Exception {
        LeaveAssaultMessage message = new LeaveAssaultMessage(
                UUID.fromString("22222222-2222-2222-2222-222222222222")
        );

        service.onCharacterLeavesAssault(message);

        verify(clientMessagingService).createVariables(user.getId(), message.getRequestId(), character.getId());
        verify(runtimeService).correlateMessage(eq(PlanetaryAssaultService.PLAYER_LEAVES_ASSAULT_MESSAGE), anyString(), any());
        verify(applicationContext, never()).getBean(UserErrorMessage.class);
    }

    @Test
    public void onCharacterLeaveAssault_Fail() throws Exception {
        LeaveAssaultMessage message = new LeaveAssaultMessage(
                UUID.fromString("22222222-2222-2222-2222-222222222222")
        );

        doThrow(MismatchingMessageCorrelationException.class).when(runtimeService).correlateMessage(anyString(), anyString(), any());

        service.onCharacterLeavesAssault(message);

        verify(clientMessagingService).createVariables(user.getId(), message.getRequestId(), character.getId());
        verify(runtimeService).correlateMessage(eq(PlanetaryAssaultService.PLAYER_LEAVES_ASSAULT_MESSAGE), anyString(), any());
        verify(clientMessagingService).sendToUser(any(), any(User.class));
    }

    @Test
    public void updateOpenGames() {
        service.updateOpenGames();
        verify(runtimeService).signalEventReceived(PlanetaryAssaultService.UPDATE_OPEN_GAMES_SIGNAL);
    }

//    @Test
//    public void onGameResult() {
//        int fafId = 5;
//        when(characterRepository.findActiveCharacterByFafId(fafId)).thenReturn(Optional.of(character));
//        when(character.getId()).thenReturn(UUID.fromString("33333333-3333-3333-3333-333333333333"));
//        when(character.getFaction()).thenReturn(Faction.UEF);
//
//        GameResultMessage result = new GameResultMessage(
//                123456,
//                false,
//                Set.of(new GameResultMessage.PlayerResult(fafId, true, false))
//        );
//        service.onGameResult(result);
//        verify(runtimeService).correlateMessage(eq(PlanetaryAssaultService.GAME_RESULT_MESSAGE), anyString(), any());
//    }
}
