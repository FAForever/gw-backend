package com.faforever.gw.bpmn.services;

import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.inbound.InitiateAssaultMessage;
import com.faforever.gw.messaging.client.inbound.JoinAssaultMessage;
import com.faforever.gw.messaging.client.inbound.LeaveAssaultMessage;
import com.faforever.gw.messaging.client.outbound.ErrorMessage;
import com.faforever.gw.messaging.lobby.inbound.GameInfoMessage;
import com.faforever.gw.messaging.lobby.inbound.GameResultMessage;
import com.faforever.gw.messaging.lobby.inbound.PlayerSubMessage;
import com.faforever.gw.messaging.lobby.inbound.PlayersMessage;
import com.faforever.gw.model.*;
import com.faforever.gw.model.repository.BattleRepository;
import com.faforever.gw.model.repository.CharacterRepository;
import com.faforever.gw.model.repository.PlanetRepository;
import com.faforever.gw.security.User;
import com.faforever.gw.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.MessageFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
/**
 * Service class for the BPMN process "planetary assault"
 */
public class PlanetaryAssaultService {
    public static final String UPDATE_OPEN_GAMES_SIGNAL = "Signal_UpdateOpenGames";
    public static final String INITIATE_ASSAULT_MESSAGE = "Message_InitiateAssault";
    public static final String PLAYER_JOINS_ASSAULT_MESSAGE = "Message_PlayerJoinsAssault";
    public static final String PLAYER_LEAVES_ASSAULT_MESSAGE = "Message_PlayerLeavesAssault";
    public static final String GAME_RESULT_MESSAGE = "Message_GameResult";
    public static final Long XP_MALUS_FOR_RECALL = 5L;
    private final ProcessEngine processEngine;
    private final RuntimeService runtimeService;
    private final ClientMessagingService clientMessagingService;
    private final BattleRepository battleRepository;
    private final PlanetRepository planetRepository;
    private final CharacterRepository characterRepository;
    private final UserService userService;

    @Transactional(dontRollbackOn = BpmnError.class)
    @EventListener
    public void onCharacterInitiatesAssault(InitiateAssaultMessage message) {
        User user = userService.getUserFromContext();
        log.debug("onCharacterInitiatesAssault by user {}", user.getId());
        UUID battleUUID = UUID.randomUUID();

        GwCharacter character = userService.getActiveCharacter(user);
        Planet planet = planetRepository.getOne(message.getPlanetId());

        // TODO: How do we handle NullPointerException i.e. if planet is null?

        VariableMap variables = clientMessagingService.createVariables(user.getId(), message.getRequestId(), user.getActiveCharacter().getId())
                .putValue("battle", battleUUID)
                .putValue("planet", planet.getId())
                .putValue("attackingFaction", character.getFaction())
                .putValue("defendingFaction", planet.getCurrentOwner())
                .putValue("attackerCount", 1)
                .putValue("defenderCount", 0)
                .putValue("gameFull", false)
                .putValue("waitingProgress", 0.0d);

        log.debug("-> added processVariables: {}", variables);
        runtimeService.startProcessInstanceByMessage(INITIATE_ASSAULT_MESSAGE, battleUUID.toString(), variables);
    }

    @EventListener
    public void onCharacterJoinsAssault(JoinAssaultMessage message) {
        User user = userService.getUserFromContext();
        log.debug("onCharacterJoinsAssault for battle {}", message.getBattleId());

        VariableMap variables = clientMessagingService.createVariables(user.getId(), message.getRequestId(), userService.getActiveCharacter(user).getId());

        try {
            runtimeService.correlateMessage(PLAYER_JOINS_ASSAULT_MESSAGE, message.getBattleId().toString(), variables);
        } catch (MismatchingMessageCorrelationException e) {
            log.error("Battle {} is no active bpmn instance", message.getBattleId());
            sendErrorToUser(user, message.getRequestId(), GwErrorType.BATTLE_INVALID);
        }
    }

    @EventListener
    public void onCharacterLeavesAssault(LeaveAssaultMessage message) {
        User user = userService.getUserFromContext();
        log.debug("onCharacterLeavesAssault for battle {}", message.getBattleId());

        VariableMap variables = clientMessagingService.createVariables(user.getId(), message.getRequestId(), userService.getActiveCharacter(user).getId());

        try {
            runtimeService.correlateMessage(PLAYER_LEAVES_ASSAULT_MESSAGE, message.getBattleId().toString(), variables);
        } catch (MismatchingMessageCorrelationException e) {
            log.error("Battle {} is no active bpmn instance", message.getBattleId());
            sendErrorToUser(user, message.getRequestId(), GwErrorType.BATTLE_INVALID);
        }
    }

    private void sendErrorToUser(User user, UUID requestId, GwErrorType errorType) {
        clientMessagingService.sendToUser(new ErrorMessage(requestId, errorType.getErrorCode(), errorType.getErrorMessage()), user);
    }

    @Scheduled(fixedDelay = 60000)
    public void updateOpenGames() {
        runtimeService.signalEventReceived(UPDATE_OPEN_GAMES_SIGNAL);
    }

    @EventListener
    @Transactional(dontRollbackOn = BpmnError.class)
    public void onGameResult(GameResultMessage gameResultMessage) {
        log.debug("onGameResult for game id: {}", gameResultMessage.getGameId());
        Battle battle = battleRepository.findOneByFafGameId(gameResultMessage.getGameId())
                .orElseThrow(() -> new IllegalStateException("Lobby server sent game result for unknown game id: " + gameResultMessage.getGameId()));

        // The lobby server does not know about GW characters and factions, so we need to convert manually
        GameResult gameResult = new GameResult();
        gameResult.setBattle(battle.getId());

        HashMap<Integer, GwCharacter> fafUserIdToCharacter = new HashMap<>();

        gameResultMessage.getPlayerResults().forEach(
                playerResult -> {
                    int fafId = playerResult.getPlayerId();
                    GwCharacter character = characterRepository.findActiveCharacterByFafId(fafId).orElse(null);

                    if (playerResult.isWinner()) {
                        if (gameResult.getWinner() != null && gameResult.getWinner() != character.getFaction()) {
                            String errorMessage = MessageFormat.format("For battle {0} multiple factions are listed as winner (message={1})", battle.getId(), gameResultMessage);
                            log.error(errorMessage);
                            throw new IllegalStateException(errorMessage);
                        }

                        gameResult.setWinner(character.getFaction());
                    }

                    fafUserIdToCharacter.put(fafId, character);
                }
        );

        if (gameResult.getWinner() == null) {
            String errorMessage = MessageFormat.format("For battle {0} no winning faction could be determined (message={1})", battle.getId(), gameResultMessage);
            log.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        List<GameCharacterResult> characterResults = new ArrayList<>();

        gameResultMessage.getPlayerResults().forEach(
                playerResult -> {
                    BattleParticipantResult battleParticipantResult;

                    if (playerResult.isAcuKilled()) {
                        battleParticipantResult = BattleParticipantResult.DEATH;
                    } else if (playerResult.isWinner()) {
                        battleParticipantResult = BattleParticipantResult.VICTORY;
                    } else {
                        battleParticipantResult = BattleParticipantResult.RECALL;
                    }

                    characterResults.add(
                            new GameCharacterResult(
                                    fafUserIdToCharacter.get(playerResult.getPlayerId()).getId(),
                                    battleParticipantResult,
                                    null)
                    );
                }
        );

        gameResult.setCharacterResults(characterResults);

        VariableMap variables = Variables.createVariables()
                .putValue("gameResult", gameResult);

        runtimeService.correlateMessage(GAME_RESULT_MESSAGE, battle.getId().toString(), variables);
    }

    public Long calcFactionVictoryXpForCharacter(Battle battle, GwCharacter character) {
        Optional<BattleParticipant> participantOptional = battle.getParticipant(character);

        if (participantOptional.isPresent()) {
            BattleParticipant participant = participantOptional.get();

            Long noOfAllies = battle.getParticipants().stream()
                    .filter(battleParticipant -> battleParticipant.getFaction() == character.getFaction())
                    .count();

            Long noOfEnemies = battle.getParticipants().stream()
                    .filter(battleParticipant -> battleParticipant.getFaction() != character.getFaction())
                    .count();

            if (battle.getWinningFaction() == character.getFaction()) {
                Long gainedXP = Math.round(10.0 * noOfEnemies / Math.pow(noOfAllies * 0.9, noOfAllies - 1));

                if (participant.getResult() == BattleParticipantResult.RECALL) {
                    gainedXP -= XP_MALUS_FOR_RECALL;
                }

                return gainedXP;
            } else {
                return 0L;
            }
        } else {
            throw new RuntimeException(String.format("Character %s didn't participate in battle %s", character.getId(), battle.getId()));
        }
    }

    public Long calcTeamkillXpMalus(GwCharacter character) {
        return Math.round(30.0 / Math.pow(0.88, character.getRank().getLevel()));
    }

    public Long calcKillXpBonus(GwCharacter killer, GwCharacter victim) {
        // Killing an ACU is worth 5 points per Rank, with an added factor for drop off with higher ranked players
        // + 5% bonus per rank the victim was higher than the killer

        Integer killerRank = killer.getRank().getLevel();
        Double rankDifferenceFactor = Math.min(0.0, victim.getRank().getLevel() - killerRank) * 5 / 100.0;
        return Math.round(5 * killerRank * Math.pow(0.99, killerRank - 1) * rankDifferenceFactor);
    }

    public double calcWaitingProgress(int mapSlots, long attackerCount, long defenderCount) {
        VariableMap attackerVariables = Variables.createVariables()
                .putValue("map_slots", mapSlots)
                .putValue("faction_player_count", attackerCount);

        VariableMap defenderVariables = Variables.createVariables()
                .putValue("map_slots", mapSlots)
                .putValue("faction_player_count", defenderCount);

        DecisionService decisionService = processEngine.getDecisionService();
        DmnDecisionTableResult attackerResult = decisionService.evaluateDecisionTableByKey("assault_progress_factor", attackerVariables);
        DmnDecisionTableResult defenderResult = decisionService.evaluateDecisionTableByKey("assault_progress_factor", defenderVariables);

        // we can securely access getFirstResult, because the DMN table gives a unique result
        Double attackerProgress = attackerResult.getFirstResult().getEntry("progress_factor");
        Double defenderProgress = defenderResult.getFirstResult().getEntry("progress_factor");

        Double progressNormalizer = mapSlots * 20.0;

        return (attackerCount * attackerProgress + defenderCount * defenderProgress) / progressNormalizer;
    }

    @Transactional(dontRollbackOn = BpmnError.class)
    public void onMatchCreated(Battle detachedBattle, long gameId) {
        log.debug("Match created for battle ''{}'' with faf game id: {}", detachedBattle.getId(), gameId);
        Battle battle = battleRepository.getOne(detachedBattle.getId());
        battle.setFafGameId(gameId);
        battleRepository.save(battle);
    }

    @EventListener
    public void onGameInfo(GameInfoMessage gameInfoMessage) {
        log.trace("Checking relevance of GameInfo for game id: {}", gameInfoMessage.getId());

        Optional<Battle> battleOptional = battleRepository.findOneByFafGameId(gameInfoMessage.getId());

        if (!battleOptional.isPresent()) {
            log.debug("GameInfo for game id '{}' ignored, no corresponding battle", gameInfoMessage.getId());
            return;
        }

        Battle battle = battleOptional.get();
        log.debug("GameInfo for game id '{}' belongs to battle id '{}'", battle.getId());

        // TODO: Update information about state of the game (playing)
    }

    @EventListener
    public void onPlayersInfo(PlayersMessage playersMessage) {
        for (PlayerSubMessage playerSubMessage : playersMessage.getPlayers()) {

        }
    }
}
