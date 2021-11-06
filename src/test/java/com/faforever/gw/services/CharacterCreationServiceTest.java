package com.faforever.gw.services;

import com.faforever.gw.bpmn.services.CharacterCreationService;
import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.inbound.RequestCharacterMessage;
import com.faforever.gw.messaging.client.inbound.SelectCharacterNameMessage;
import com.faforever.gw.model.Faction;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.security.User;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CharacterCreationServiceTest {
    @Mock
    private RuntimeService runtimeService;
    @Mock
    private ClientMessagingService clientMessagingService;
    @Mock
    private UserService userService;
    @Mock
    private CharacterRepository characterRepository;
    @Mock
    private User user;

    private CharacterCreationService service;

    @BeforeEach
    public void setUp() throws Exception {
        lenient().when(userService.getUserFromContext()).thenReturn(user);
        lenient().when(user.getId()).thenReturn(5L);

        service = new CharacterCreationService(runtimeService, clientMessagingService, userService, characterRepository);
    }

    @Test
    public void testOnRequestCharacter() throws Exception {
        RequestCharacterMessage message = new RequestCharacterMessage(
                Faction.UEF);

        VariableMap map = Variables.createVariables();
        when(clientMessagingService.createVariables(anyLong(), any(), any())).thenReturn(map);

        service.onRequestCharacter(message);

        verify(clientMessagingService).createVariables(user.getId(), message.getRequestId(), null);
        verify(runtimeService).correlateMessage(eq(CharacterCreationService.REQUEST_CHARACTER_MESSAGE), anyString(), any());

        assertEquals(map.get("requestedFaction"), message.getFaction());
    }

    @Test
    public void testOnSelectCharacterName() throws Exception {
        SelectCharacterNameMessage message = new SelectCharacterNameMessage(
                "newName");

        service.onSelectCharacterName(message);

        verify(runtimeService).correlateMessage(eq(CharacterCreationService.RECEIVE_CHARACTER_NAME_SELECTION_MESSAGE), anyString(), any());
    }


}
