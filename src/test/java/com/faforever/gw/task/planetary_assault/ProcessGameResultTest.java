package com.faforever.gw.task.planetary_assault;

import com.faforever.gw.bpmn.services.PlanetaryAssaultService;
import com.faforever.gw.bpmn.task.planetary_assault.ProcessGameResultTask;
import com.faforever.gw.model.*;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProcessGameResultTest {
    @Mock
    private DelegateExecution delegateExecution;
    @Mock
    private PlanetaryAssaultService planetaryAssaultService;
    @Mock
    private CharacterRepository characterRepository;
    @Mock
    private BattleRepository battleRepository;
    @Mock
    private Battle battle;
    @Mock
    private Planet planet;
    @Mock
    private Map map;
    private GameResult gameResult;
    private List<GameCharacterResult> characterResults = new ArrayList<>();
    private ProcessGameResultTask task;
    private ArrayList<BattleParticipant> battleParticipants = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        when(delegateExecution.getProcessInstance()).thenReturn(delegateExecution);
        when(delegateExecution.getBusinessKey()).thenReturn("test");

        when(delegateExecution.getVariable("battle"))
                .thenReturn(UUID.fromString("01234567-9012-3456-7890-123456789012"));
        when(delegateExecution.getVariable("attackingFaction")).thenReturn(Faction.UEF);

        gameResult = new GameResult();
        gameResult.setBattle(UUID.fromString("01234567-9012-3456-7890-123456789012"));
        gameResult.setCharacterResults(characterResults);
        when(delegateExecution.getVariable("gameResult")).thenReturn(gameResult);

        when(battleRepository.findOne(any(UUID.class))).thenReturn(battle);
        when(battle.getAttackingFaction()).thenReturn(Faction.UEF);
        when(battle.getDefendingFaction()).thenReturn(Faction.CYBRAN);

        task = new ProcessGameResultTask(planetaryAssaultService, battleRepository, characterRepository);

        when(battle.getParticipants()).thenReturn(battleParticipants);
    }

    /***
     * Simple test case with 2 players:
     * Attacker won, defender recalled, no kills
     */
    @Test
    public void testAttackerWin() throws Exception {
        gameResult.setWinner(Faction.UEF);

        CharacterResultDataSet attacker1 = new CharacterResultDataSet(
                UUID.fromString("aaaaaaaa-1111-1111-1111-111111111111"),
                Faction.UEF,
                BattleRole.ATTACKER,
                1,
                BattleParticipantResult.VICTORY
        );

        CharacterResultDataSet defender1 = new CharacterResultDataSet(
                UUID.fromString("dddddddd-1111-1111-1111-111111111111"),
                Faction.CYBRAN,
                BattleRole.DEFENDER,
                5,
                BattleParticipantResult.RECALL
        );

        characterResults.add(attacker1.result);
        characterResults.add(defender1.result);

        task.execute(delegateExecution);

        // Assert input taken from bpmn process
        verify(delegateExecution, atLeastOnce()).getVariable("battle");

        // Assert gained XP
        verify(attacker1.character).setXp(any());
        verify(defender1.character, never()).setXp(any());

        // Assert process variables
        verify(delegateExecution).setVariable("winner", "attacker");
    }

    /***
     * Simple test case with 2 players:
     * Defender won, attacker died, 1 kill
     */
    @Test
    public void testDefenderWin() throws Exception {
        gameResult.setWinner(Faction.CYBRAN);

        CharacterResultDataSet attacker1 = new CharacterResultDataSet(
                UUID.fromString("aaaaaaaa-1111-1111-1111-111111111111"),
                Faction.UEF,
                BattleRole.ATTACKER,
                1,
                BattleParticipantResult.DEATH
        );

        CharacterResultDataSet defender1 = new CharacterResultDataSet(
                UUID.fromString("dddddddd-1111-1111-1111-111111111111"),
                Faction.CYBRAN,
                BattleRole.DEFENDER,
                5,
                BattleParticipantResult.VICTORY
        );

        characterResults.add(attacker1.result);
        characterResults.add(defender1.result);
        attacker1.result.setKilledByCharacter(defender1.id);

        task.execute(delegateExecution);

        // Assert input taken from bpmn process
        verify(delegateExecution, atLeastOnce()).getVariable("battle");

        // Assert gained XP
        verify(attacker1.character, never()).setXp(any());
        verify(defender1.character, times(2)).setXp(any());

        // Assert process variables
        verify(delegateExecution).setVariable("winner", "defender");
    }

    @Test
    public void testComplex() throws Exception {
        gameResult.setWinner(Faction.UEF);

        CharacterResultDataSet attacker1 = new CharacterResultDataSet(
                UUID.fromString("aaaaaaaa-1111-1111-1111-111111111111"),
                Faction.UEF,
                BattleRole.ATTACKER,
                1,
                BattleParticipantResult.VICTORY
        );

        CharacterResultDataSet attacker2 = new CharacterResultDataSet(
                UUID.fromString("aaaaaaaa-2222-2222-2222-222222222222"),
                Faction.UEF,
                BattleRole.ATTACKER,
                4,
                BattleParticipantResult.DEATH
        );

        CharacterResultDataSet attacker3 = new CharacterResultDataSet(
                UUID.fromString("aaaaaaaa-3333-3333-3333-333333333333"),
                Faction.UEF,
                BattleRole.ATTACKER,
                4,
                BattleParticipantResult.DEATH
        );

        CharacterResultDataSet defender1 = new CharacterResultDataSet(
                UUID.fromString("dddddddd-1111-1111-1111-111111111111"),
                Faction.CYBRAN,
                BattleRole.DEFENDER,
                5,
                BattleParticipantResult.RECALL
        );

        CharacterResultDataSet defender2 = new CharacterResultDataSet(
                UUID.fromString("dddddddd-2222-2222-2222-222222222222"),
                Faction.CYBRAN,
                BattleRole.DEFENDER,
                2,
                BattleParticipantResult.DEATH
        );

        characterResults.add(attacker1.result);
        characterResults.add(attacker2.result);
        characterResults.add(defender1.result);
        characterResults.add(defender2.result);
        attacker2.result.setKilledByCharacter(defender1.id);
        attacker3.result.setKilledByCharacter(attacker2.id);
        defender1.result.setKilledByCharacter(defender1.id);

        task.execute(delegateExecution);

        // Assert input taken from bpmn process
        verify(delegateExecution, atLeastOnce()).getVariable("battle");

        // Assert gained XP
        verify(attacker1.character).setXp(any());
        verify(attacker2.character, never()).setXp(any());
        verify(attacker3.character, never()).setXp(any());
        verify(defender1.character, times(2)).setXp(any());
        verify(defender2.character, never()).setXp(any());

        // Assert process variables
        verify(delegateExecution).setVariable("winner", "attacker");
    }

    private class CharacterResultDataSet {
        public UUID id;
        public GwCharacter character;
        public BattleParticipant participant;
        public GameCharacterResult result;

        public CharacterResultDataSet(UUID id, Faction faction, BattleRole battleRole, int rank, BattleParticipantResult result) {
            this.id = id;
            this.character = mock(GwCharacter.class);
            this.participant = mock(BattleParticipant.class);
            this.result = new GameCharacterResult(id, result, null);
            when(participant.getCharacter()).thenReturn(character);

            when(character.getId()).thenReturn(id);
            when(character.getFaction()).thenReturn(faction);
            when(participant.getFaction()).thenReturn(faction);

            Rank r = mock(Rank.class);
            when(character.getRank()).thenReturn(r);
            when(r.getLevel()).thenReturn(rank);

            when(participant.getResult()).thenReturn(result);
            when(ProcessGameResultTest.this.battle.getParticipant(character)).thenReturn(Optional.of(participant));

            when(ProcessGameResultTest.this.characterRepository.findOne(id)).thenReturn(character);
        }
    }

}