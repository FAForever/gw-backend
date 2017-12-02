package com.faforever.gw.services;

import com.faforever.gw.bpmn.services.CharacterCreationService;
import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.inbound.RequestCharacterMessage;
import com.faforever.gw.messaging.client.inbound.SelectCharacterNameMessage;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.security.User;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class CharacterCreationServiceTest {
    @Mock
    private ProcessEngine processEngine;
    @Mock
    private RuntimeService runtimeService;
    @Mock
    private ClientMessagingService clientMessagingService;
    @Mock
    private CharacterRepository characterRepository;
    @Mock
    private User user;

    private CharacterCreationService service;

    @Before
    public void setUp() throws Exception {
        when(user.getId()).thenReturn(5L);

        service = new CharacterCreationService(processEngine, runtimeService, clientMessagingService, characterRepository);
    }

    @Test
    public void testOnRequestCharacter() throws Exception {
        RequestCharacterMessage message = new RequestCharacterMessage(
                Faction.UEF);

        VariableMap map = Variables.createVariables();
        when(clientMessagingService.createVariables(anyLong(), any(), any())).thenReturn(map);

        service.onRequestCharacter(message, user);

        verify(clientMessagingService).createVariables(user.getId(), message.getRequestId(), null);
        verify(runtimeService).correlateMessage(eq(CharacterCreationService.REQUEST_CHARACTER_MESSAGE), anyString(), any());

        assertEquals(map.get("requestedFaction"), message.getFaction());
    }

    @Test
    public void testOnSelectCharacterName() throws Exception {
        SelectCharacterNameMessage message = new SelectCharacterNameMessage(
                "newName");

        service.onSelectCharacterName(message, user);

        verify(runtimeService).correlateMessage(eq(CharacterCreationService.RECEIVE_CHARACTER_NAME_SELECTION_MESSAGE), anyString(), any());
    }


}
