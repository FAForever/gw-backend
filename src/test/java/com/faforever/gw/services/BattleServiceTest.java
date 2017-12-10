package com.faforever.gw.services;


import com.faforever.gw.bpmn.services.PlanetaryAssaultService;
import com.faforever.gw.model.*;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.service.BattleService;
import com.faforever.gw.model.service.CharacterService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BattleServiceTest {
    @Mock
    private BattleRepository battleRepository;
    @Mock
    private PlanetaryAssaultService planetaryAssaultService;
    @Mock
    private CharacterService characterService;
    @Mock
    private Battle battle;
    @Mock
    private Planet planet;

    private BattleService service;

    private GameResult gameResult;
    private List<GameCharacterResult> characterResults;
    private ArrayList<BattleParticipant> battleParticipants;

    @Before
    public void setUp() {
        service = new BattleService(battleRepository, planetaryAssaultService, characterService);

        characterResults = new ArrayList<>();
        battleParticipants = new ArrayList<>();

        gameResult = new GameResult();
        gameResult.setBattle(UUID.fromString("bbbbbbbb-0000-0000-0000-000000000000"));
        gameResult.setCharacterResults(characterResults);

        when(battleRepository.findOne(any(UUID.class))).thenReturn(battle);
        when(battle.getAttackingFaction()).thenReturn(Faction.UEF);
        when(battle.getDefendingFaction()).thenReturn(Faction.CYBRAN);
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
                battle,
                UUID.fromString("aaaaaaaa-1111-1111-1111-111111111111"),
                Faction.UEF,
                BattleRole.ATTACKER,
                1,
                BattleParticipantResult.VICTORY
        );

        CharacterResultDataSet defender1 = new CharacterResultDataSet(
                battle,
                UUID.fromString("dddddddd-1111-1111-1111-111111111111"),
                Faction.CYBRAN,
                BattleRole.DEFENDER,
                5,
                BattleParticipantResult.RECALL
        );

        service.processGameResult(gameResult);

        // Assert gained XP
        verify(attacker1.character).setXp(any());
        verify(defender1.character, never()).setXp(any());
    }

    /***
     * Simple test case with 2 players:
     * Defender won, attacker died, 1 kill
     */
    @Test
    public void testDefenderWin() throws Exception {
        gameResult.setWinner(Faction.CYBRAN);

        CharacterResultDataSet attacker1 = new CharacterResultDataSet(
                battle,
                UUID.fromString("aaaaaaaa-1111-1111-1111-111111111111"),
                Faction.UEF,
                BattleRole.ATTACKER,
                1,
                BattleParticipantResult.DEATH
        );

        CharacterResultDataSet defender1 = new CharacterResultDataSet(
                battle,
                UUID.fromString("dddddddd-1111-1111-1111-111111111111"),
                Faction.CYBRAN,
                BattleRole.DEFENDER,
                5,
                BattleParticipantResult.VICTORY
        );

        attacker1.result.setKilledByCharacter(defender1.id);
        service.processGameResult(gameResult);

        // Assert gained XP
        verify(attacker1.character, never()).setXp(any());
        verify(defender1.character, times(2)).setXp(any());
    }

    @Test
    public void testComplex() throws Exception {
        gameResult.setWinner(Faction.UEF);

        CharacterResultDataSet attacker1 = new CharacterResultDataSet(
                battle,
                UUID.fromString("aaaaaaaa-1111-1111-1111-111111111111"),
                Faction.UEF,
                BattleRole.ATTACKER,
                1,
                BattleParticipantResult.VICTORY
        );

        CharacterResultDataSet attacker2 = new CharacterResultDataSet(
                battle,
                UUID.fromString("aaaaaaaa-2222-2222-2222-222222222222"),
                Faction.UEF,
                BattleRole.ATTACKER,
                4,
                BattleParticipantResult.DEATH
        );

        CharacterResultDataSet attacker3 = new CharacterResultDataSet(
                battle,
                UUID.fromString("aaaaaaaa-3333-3333-3333-333333333333"),
                Faction.UEF,
                BattleRole.ATTACKER,
                4,
                BattleParticipantResult.DEATH
        );

        CharacterResultDataSet defender1 = new CharacterResultDataSet(
                battle,
                UUID.fromString("dddddddd-1111-1111-1111-111111111111"),
                Faction.CYBRAN,
                BattleRole.DEFENDER,
                5,
                BattleParticipantResult.RECALL
        );

        CharacterResultDataSet defender2 = new CharacterResultDataSet(
                battle,
                UUID.fromString("dddddddd-2222-2222-2222-222222222222"),
                Faction.CYBRAN,
                BattleRole.DEFENDER,
                2,
                BattleParticipantResult.DEATH
        );

        attacker2.result.setKilledByCharacter(defender1.id);
        attacker3.result.setKilledByCharacter(attacker2.id);
        defender1.result.setKilledByCharacter(defender1.id);

        service.processGameResult(gameResult);

        // Assert gained XP
        verify(attacker1.character).setXp(any());
        verify(attacker2.character, never()).setXp(any());
        verify(attacker3.character, never()).setXp(any());
        verify(defender1.character, times(2)).setXp(any());
        verify(defender2.character, never()).setXp(any());
    }

    public class CharacterResultDataSet {
        public UUID id;
        public GwCharacter character;
        public BattleParticipant participant;
        public GameCharacterResult result;

        public CharacterResultDataSet(Battle battle, UUID id, Faction faction, BattleRole battleRole, int rank, BattleParticipantResult result) {
            this.id = id;
            this.character = mock(GwCharacter.class);
            this.participant = mock(BattleParticipant.class);
            this.result = new GameCharacterResult(id, result, null);

            characterResults.add(this.result);
            battleParticipants.add(participant);
            when(battle.getParticipant(character)).thenReturn(Optional.of(participant));
            when(characterService.requireCharacter(id)).thenReturn(character);

            when(participant.getCharacter()).thenReturn(character);

            when(character.getId()).thenReturn(id);
            when(character.getFaction()).thenReturn(faction);
            when(participant.getFaction()).thenReturn(faction);

            Rank r = mock(Rank.class);
            when(character.getRank()).thenReturn(r);
            when(r.getLevel()).thenReturn(rank);

            when(participant.getResult()).thenReturn(result);
            when(battle.getParticipant(character)).thenReturn(Optional.of(participant));
        }
    }
}
