package com.faforever.gw.bpmn.services;

import com.faforever.gw.messaging.client.ClientMessagingService;
import com.faforever.gw.messaging.client.inbound.InitiateAssaultMessage;
import com.faforever.gw.messaging.client.inbound.JoinAssaultMessage;
import com.faforever.gw.messaging.client.inbound.LeaveAssaultMessage;
import com.faforever.gw.messaging.client.outbound.ErrorMessage;
import com.faforever.gw.model.Battle;
import com.faforever.gw.model.BattleParticipant;
import com.faforever.gw.model.BattleParticipantResult;
import com.faforever.gw.model.BattleResult;
import com.faforever.gw.model.BattleRole;
import com.faforever.gw.model.GameCharacterResult;
import com.faforever.gw.model.GameResult;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Planet;
import com.faforever.gw.model.repository.BattleRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for the BPMN process "planetary assault"
 */
@Slf4j
@Service
@RequiredArgsConstructor
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
    private final UserService userService;

    @Transactional(dontRollbackOn = BpmnError.class)
    @EventListener
    public void onCharacterInitiatesAssault(InitiateAssaultMessage message) {
        User user = userService.getUserFromContext();
        GwCharacter character = user.getActiveCharacter()
                .orElseThrow(() -> new IllegalStateException("User has no active character: " + user.getId()));
        UUID battleUUID = UUID.randomUUID();

        log.debug("onCharacterInitiatesAssault by user {}", user.getId());

        Planet planet = planetRepository.findById(message.getPlanetId())
                .orElseThrow(() -> new IllegalStateException("Unknown planet id: " + message.getPlanetId()));

        VariableMap variables = clientMessagingService.createVariables(user.getId(), message.getRequestId(), character.getId())
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
        GwCharacter character = user.getActiveCharacter()
                .orElseThrow(() -> new IllegalStateException("User has no active character: " + user.getId()));
        log.debug("onCharacterJoinsAssault for battle {}", message.getBattleId());

        VariableMap variables = clientMessagingService.createVariables(user.getId(), message.getRequestId(), character.getId());

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
        GwCharacter character = user.getActiveCharacter()
                .orElseThrow(() -> new IllegalStateException("User has no active character: " + user.getId()));
        log.debug("onCharacterLeavesAssault for battle {}", message.getBattleId());

        VariableMap variables = clientMessagingService.createVariables(user.getId(), message.getRequestId(), character.getId());

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
    public void onBattleResult(BattleResult battleResult) {
        log.debug("onGameResult for battle id: {}", battleResult.battleId());

        Battle battle = battleRepository.findById(battleResult.battleId())
                .orElseThrow(() -> new IllegalStateException("Unknown battle id: " + battleResult.battleId()));

        GameResult gameResult = new GameResult();
        gameResult.setBattle(battle.getId());
        gameResult.setWinner(switch (battleResult.winningTeam().orElse(BattleRole.DEFENDER)) {
            case ATTACKER -> battle.getAttackingFaction();
            case DEFENDER -> battle.getDefendingFaction();
        });

        List<GameCharacterResult> gameCharacterResults = battleResult.participantResults().entrySet().stream()
                .map(entry -> {
                    UUID characterId = entry.getKey();
                    BattleParticipantResult result = entry.getValue();

                    return new GameCharacterResult(characterId, result, null);
                })
                .toList();

        gameResult.setCharacterResults(gameCharacterResults);

        VariableMap variables = Variables.createVariables()
                .putValue("gameResult", gameResult);

        runtimeService.correlateMessage(GAME_RESULT_MESSAGE, battle.getId().toString(), variables);
    }

    public long calcFactionVictoryXpForCharacter(Battle battle, GwCharacter character) {
        Optional<BattleParticipant> participantOptional = battle.getParticipant(character);

        if (participantOptional.isPresent()) {
            BattleParticipant participant = participantOptional.get();

            long noOfAllies = battle.getParticipants().stream()
                    .filter(battleParticipant -> battleParticipant.getFaction() == character.getFaction())
                    .count();

            long noOfEnemies = battle.getParticipants().stream()
                    .filter(battleParticipant -> battleParticipant.getFaction() != character.getFaction())
                    .count();

            if (battle.getWinningFaction() == character.getFaction()) {
               long gainedXP = Math.round(10.0 * noOfEnemies / Math.pow(noOfAllies * 0.9, noOfAllies - 1));

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

    public long calcTeamkillXpMalus(GwCharacter character) {
        return Math.round(30.0 / Math.pow(0.88, character.getRank().getLevel()));
    }

    public long calcKillXpBonus(GwCharacter killer, GwCharacter victim) {
        // Killing an ACU is worth 5 points per Rank, with an added factor for drop off with higher ranked players
        // + 5% bonus per rank the victim was higher than the killer

        int killerRank = killer.getRank().getLevel();
        double rankDifferenceFactor = Math.min(0.0, victim.getRank().getLevel() - killerRank) * 5 / 100.0;
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
        double attackerProgress = attackerResult.getFirstResult().getEntry("progress_factor");
        double defenderProgress = defenderResult.getFirstResult().getEntry("progress_factor");

        double progressNormalizer = mapSlots * 20.0;

        return (attackerCount * attackerProgress + defenderCount * defenderProgress) / progressNormalizer;
    }

    @Transactional(dontRollbackOn = BpmnError.class)
    public void onMatchCreated(Battle detachedBattle, long gameId) {
        log.debug("Match created for battle ''{}'' with faf game id: {}", detachedBattle.getId(), gameId);
        Battle battle = battleRepository.findById(detachedBattle.getId())
                .orElseThrow(() -> new IllegalStateException("Unknown battle id: " + detachedBattle.getId()));
        battle.setFafGameId(gameId);
        battleRepository.save(battle);
    }

}
