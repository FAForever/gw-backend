package com.faforever.gw.model;

import com.faforever.gw.bpmn.task.InitiateAssaultTask;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidationHelperTest {
    ValidationHelper validationHelper;

    @Before
    public void setUp() throws Exception {
        validationHelper = new ValidationHelper();
    }

    public void validateCharacterInBattle__ExpectTrue_Success() {
        GwCharacter character = mock(GwCharacter.class);
        Battle battle = mock(Battle.class);
        List<BattleParticipant> battleParticipantList = mock(List.class);

        when(character.getBattleParticipantList()).thenReturn(battleParticipantList);

         validationHelper.validateCharacterInBattle(character, battle, true);
    }
}
