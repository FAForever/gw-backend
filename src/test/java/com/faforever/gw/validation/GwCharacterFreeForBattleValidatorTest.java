package com.faforever.gw.validation;

import com.faforever.gw.model.Battle;
import com.faforever.gw.model.BattleParticipant;
import com.faforever.gw.model.BattleStatus;
import com.faforever.gw.model.GwCharacter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GwCharacterFreeForBattleValidatorTest {
    CharacterFreeForBattleValidator characterFreeForBattleValidator;
    @Mock
    ConstraintValidatorContext constraintValidatorContext;
    @Mock
    ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;
    @Mock
    BattleParticipant battleParticipant;
    @Mock
    GwCharacter gwCharacter;
    @Mock
    Battle battle;
    List<BattleParticipant> battleParticipants;

    @Before
    public void setUp() throws Exception {
        characterFreeForBattleValidator = new CharacterFreeForBattleValidator();

        battleParticipants = new ArrayList<>();

        when(battleParticipant.getCharacter())
                .thenReturn(gwCharacter);
        when(battleParticipant.getBattle())
                .thenReturn(battle);
        when(gwCharacter.getBattleParticipantList())
                .thenReturn(battleParticipants);

        battleParticipants.add(battleParticipant);

        when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(constraintViolationBuilder);
    }

    @Test
    public void isValidforEmptyList() throws  Exception {
        // by marking it CANCELED it will be ignored in the iteration
        when(battle.getStatus())
                .thenReturn(BattleStatus.CANCELED);

        assertThat(characterFreeForBattleValidator.isValid(battleParticipant, constraintValidatorContext), is(true));
    }

    @Test
    public void isValid_forOneOpenBattle() throws Exception {
         when(battle.getStatus())
                .thenReturn(BattleStatus.RUNNING);

        assertThat(characterFreeForBattleValidator.isValid(battleParticipant, constraintValidatorContext), is(true));
    }


    @Test
    public void isValid_forTwoOpenBattles() throws Exception {
        // simulate a second running battle
        battleParticipants.add(battleParticipant);

        when(battle.getStatus())
                .thenReturn(BattleStatus.RUNNING);

        assertThat(characterFreeForBattleValidator.isValid(battleParticipant, constraintValidatorContext), is(false));
    }

    @Test
    public void isValid_IgnoreNonRunning() throws Exception {
        when(battle.getStatus())
                .thenReturn(BattleStatus.INITIATED);

        Battle battle2 = mock(Battle.class);
        when(battle2.getStatus()).thenReturn(BattleStatus.CANCELED);
        BattleParticipant battleParticipant2 = mock(BattleParticipant.class);
        when(battleParticipant2.getBattle()).thenReturn(battle2);

        Battle battle3 = mock(Battle.class);
        when(battle3.getStatus()).thenReturn(BattleStatus.FINISHED);
        BattleParticipant battleParticipant3 = mock(BattleParticipant.class);
        when(battleParticipant3.getBattle()).thenReturn(battle3);

        battleParticipants.add(battleParticipant2);
        battleParticipants.add(battleParticipant3);


        assertThat(characterFreeForBattleValidator.isValid(battleParticipant, constraintValidatorContext), is(true));
    }

}