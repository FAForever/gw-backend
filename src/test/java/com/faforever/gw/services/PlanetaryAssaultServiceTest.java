package com.faforever.gw.services;

import com.faforever.gw.bpmn.message.generic.UserErrorMessage;
import com.faforever.gw.bpmn.services.PlanetaryAssaultService;
import com.faforever.gw.model.BattleParticipantResult;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import com.faforever.gw.security.GwUserRegistry;
import com.faforever.gw.security.User;
import com.faforever.gw.services.messaging.client.MessagingService;
import com.faforever.gw.services.messaging.client.incoming.InitiateAssaultMessage;
import com.faforever.gw.services.messaging.client.incoming.JoinAssaultMessage;
import com.faforever.gw.services.messaging.client.incoming.LeaveAssaultMessage;
import com.faforever.gw.services.messaging.lobby_server.incoming.GamePlayerResult;
import com.faforever.gw.services.messaging.lobby_server.incoming.GameResultMessage;
import com.google.common.collect.ImmutableList;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlanetaryAssaultServiceTest {
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private ProcessEngine processEngine;
    @Mock
    private RuntimeService runtimeService;
    @Mock
    private MessagingService messagingService;
    @Mock
    private PlanetRepository planetRepository;
    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private User user;
    @Mock
    private GwCharacter character;
    @Mock
    private Planet planet;

    private PlanetaryAssaultService service;

    @Before
    public void setUp() throws Exception {
        service = new PlanetaryAssaultService(processEngine, runtimeService, messagingService, planetRepository, characterRepository);
    }

    @Test
    public void onCharacterInitiatesAssaultTest() throws Exception {
        InitiateAssaultMessage message = new InitiateAssaultMessage(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222")
        );

        when(user.getId()).thenReturn(5L);
        when(user.getActiveCharacter()).thenReturn(character);
        when(character.getId()).thenReturn(UUID.fromString("33333333-3333-3333-3333-333333333333"));
        when(character.getFaction()).thenReturn(Faction.UEF);

        when(planet.getId()).thenReturn(UUID.fromString("44444444-4444-4444-4444-444444444444"));
        when(planet.getCurrentOwner()).thenReturn(Faction.CYBRAN);

        VariableMap map = Variables.createVariables();
        when(messagingService.createVariables(anyLong(), any(), any())).thenReturn(map);
        when(planetRepository.findOne(any(UUID.class))).thenReturn(planet);

        service.onCharacterInitiatesAssault(message, user);

        verify(messagingService).createVariables(user.getId(), message.getRequestId(), character.getId());
        verify(runtimeService).startProcessInstanceByMessage(eq(PlanetaryAssaultService.INITIATE_ASSAULT_MESSAGE), anyString(), any());

        assertEquals(map.get("planet"), planet.getId());
        assertEquals(map.get("attackingFaction"), character.getFaction());
        assertEquals(map.get("defendingFaction"), planet.getCurrentOwner());
        assertEquals(map.get("attackerCount"), 1);
        assertEquals(map.get("defenderCount"), 0);
        assertEquals(map.get("gameFull"), false);
        assertEquals(map.get("waitingProgress"), 0.0d);
        assertTrue(map.get("winner").equals("t.b.d."));
    }

    @Test
    public void onCharacterJoinsAssault_Success() throws Exception {
        JoinAssaultMessage message = new JoinAssaultMessage(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222")
        );

        when(user.getId()).thenReturn(5L);
        when(user.getActiveCharacter()).thenReturn(character);

        VariableMap map = Variables.createVariables();
        when(messagingService.createVariables(anyLong(), any(), any())).thenReturn(map);
        when(planetRepository.findOne(any(UUID.class))).thenReturn(planet);

        service.onCharacterJoinsAssault(message, user);

        verify(messagingService).createVariables(user.getId(), message.getRequestId(), character.getId());
        verify(runtimeService).correlateMessage(eq(PlanetaryAssaultService.PLAYER_JOINS_ASSAULT_MESSAGE), anyString(), any());
    }

    @Test
    public void onCharacterJoinsAssault_Fail() throws Exception {
        JoinAssaultMessage message = new JoinAssaultMessage(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222")
        );

        when(user.getId()).thenReturn(5L);
        when(user.getActiveCharacter()).thenReturn(character);

        VariableMap map = Variables.createVariables();
        when(messagingService.createVariables(anyLong(), any(), any())).thenReturn(map);
        when(planetRepository.findOne(any(UUID.class))).thenReturn(planet);

        doThrow(MismatchingMessageCorrelationException.class).when(runtimeService).correlateMessage(anyString(), anyString(), any());

        UserErrorMessage userErrorMessage = new UserErrorMessage(messagingService, mock(GwUserRegistry.class));
        when(applicationContext.getBean(UserErrorMessage.class)).thenReturn(userErrorMessage);

        service.onCharacterJoinsAssault(message, user);

        verify(messagingService).createVariables(user.getId(), message.getRequestId(), character.getId());
        verify(runtimeService).correlateMessage(eq(PlanetaryAssaultService.PLAYER_JOINS_ASSAULT_MESSAGE), anyString(), any());
        verify(messagingService).send(any());
    }

    @Test
    public void onCharacterLeaveAssault_Success() throws Exception {
        LeaveAssaultMessage message = new LeaveAssaultMessage(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222")
        );

        when(user.getId()).thenReturn(5L);
        when(user.getActiveCharacter()).thenReturn(character);

        VariableMap map = Variables.createVariables();
        when(messagingService.createVariables(anyLong(), any(), any())).thenReturn(map);
        when(planetRepository.findOne(any(UUID.class))).thenReturn(planet);

        service.onCharacterLeavesAssault(message, user);

        verify(messagingService).createVariables(user.getId(), message.getRequestId(), character.getId());
        verify(runtimeService).correlateMessage(eq(PlanetaryAssaultService.PLAYER_LEAVES_ASSAULT_MESSAGE), anyString(), any());
        verify(applicationContext, never()).getBean(UserErrorMessage.class);
    }

    @Test
    public void onCharacterLeaveAssault_Fail() throws Exception {
        LeaveAssaultMessage message = new LeaveAssaultMessage(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222")
        );

        when(user.getId()).thenReturn(5L);
        when(user.getActiveCharacter()).thenReturn(character);

        VariableMap map = Variables.createVariables();
        when(messagingService.createVariables(anyLong(), any(), any())).thenReturn(map);
        when(planetRepository.findOne(any(UUID.class))).thenReturn(planet);

        doThrow(MismatchingMessageCorrelationException.class).when(runtimeService).correlateMessage(anyString(), anyString(), any());

        UserErrorMessage userErrorMessage = new UserErrorMessage(messagingService, mock(GwUserRegistry.class));
        when(applicationContext.getBean(UserErrorMessage.class)).thenReturn(userErrorMessage);

        service.onCharacterLeavesAssault(message, user);

        verify(messagingService).createVariables(user.getId(), message.getRequestId(), character.getId());
        verify(runtimeService).correlateMessage(eq(PlanetaryAssaultService.PLAYER_LEAVES_ASSAULT_MESSAGE), anyString(), any());
        verify(messagingService).send(any());
    }

    @Test
    public void updateOpenGames() {
        service.updateOpenGames();
        verify(runtimeService).signalEventReceived(PlanetaryAssaultService.UPDATE_OPEN_GAMES_SIGNAL);
    }

    @Test
    public void onGameResult() {
        long fafId = 5L;
        when(characterRepository.findActiveCharacterByFafId(fafId)).thenReturn(character);
        when(character.getId()).thenReturn(UUID.fromString("33333333-3333-3333-3333-333333333333"));
        when(character.getFaction()).thenReturn(Faction.UEF);

        GameResultMessage result = new GameResultMessage(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                123456L,
                ImmutableList.of(new GamePlayerResult(fafId, BattleParticipantResult.VICTORY, -1L))
        );
        service.onGameResult(result);
        verify(runtimeService).correlateMessage(eq(PlanetaryAssaultService.GAME_RESULT_MESSAGE), anyString(), anyMap());
    }
}
