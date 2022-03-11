package com.team9.questgame.game_phases;

import com.team9.questgame.Entities.PlayerRanks;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.*;
import com.team9.questgame.exception.IllegalGameRequest;
import com.team9.questgame.exception.IllegalGameStateException;
import com.team9.questgame.exception.PlayerJoinException;
import com.team9.questgame.exception.PlayerNotFoundException;
import com.team9.questgame.game_phases.quest.QuestPhaseController;
import com.team9.questgame.game_phases.utils.PlayerTurnService;
import com.team9.questgame.gamemanager.service.OutboundService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

@Service
public class GeneralGameController implements CardArea<StoryCards> {

    public static final int MAX_PLAYERS = 4;
    public static final int MIN_PLAYERS = 2;
    @Getter
    private final PlayerRanks victoryCondtion;
    private final Logger LOG;
    @Getter
    private final ArrayList<Players> players;
    private final ArrayList<Players> winners;
    @Getter
    private final AdventureDecks aDeck;
    @Getter
    private final StoryDecks sDeck;
    private Players activePlayer;
    private Iterator<Players> turnSequence;

    @Getter
    private StoryCards storyCard;

    @Autowired
    @Getter
    private GeneralStateMachine stateMachine;

    @Autowired
    private OutboundService outboundService;

    @Getter
    private PlayerTurnService playerTurnService;

    @Getter
    @Autowired
    private QuestPhaseController questPhaseController;

    public GeneralGameController() {
        this(PlayerRanks.KNIGHT_OF_ROUND_TABLE);
    }

    public GeneralGameController(PlayerRanks victoryRank) {
        LOG = LoggerFactory.getLogger(GeneralGameController.class);
        this.players = new ArrayList<>();
        this.victoryCondtion = victoryRank;
        this.winners = new ArrayList<>();
        aDeck = new AdventureDecks();
        sDeck = new StoryDecks();
        playerTurnService = new PlayerTurnService(players);
    }

    /**
     * This function registers a new player to the game.
     * <p>
     * It must be the ONLY way for a player to join the game
     * and acts as a gatekeeper enforcing the player limits.
     *
     * @param player The player wishing to join the game
     * @throws PlayerJoinException An error that is thrown when a player is unable to join the game. It includes a reason.
     */
    public void playerJoin(Players player) {
        if (stateMachine.getCurrentState() != GeneralStateE.SETUP) {
            throw new IllegalGameStateException("Player can only join during SETUP state");
        }

        if (players.contains(player)) {
            throw new PlayerJoinException(player, PlayerJoinException.PlayerJoinExceptionReasonCodes.DUPLICATE_JOIN_REQUEST);
        } else if (players.size() >= MAX_PLAYERS) {
            throw new PlayerJoinException(player, PlayerJoinException.PlayerJoinExceptionReasonCodes.GAME_FULL);
        }

        LOG.info("Player {name: " + player.getName() + ", playerId: " + player.getPlayerId() + "} requested to join. Result: SUCCESS");
        players.add(player);

        stateMachine.update();
    }

    public void removePlayer(Players player) throws PlayerNotFoundException, IllegalGameRequest {
        if (stateMachine.getCurrentState() != GeneralStateE.SETUP) {
            throw new IllegalGameStateException("Players can only be removed during SETUP state");
        }

        if (!players.contains(player)) {
            throw new PlayerNotFoundException(player);
        }

        LOG.info("Player {name: " + player.getName() + ", playerId: " + player.getPlayerId() + "} requested to disconnect. Result: SUCCESS");
        players.remove(player);

        stateMachine.update();
    }

    public void startGame() {
        if (stateMachine.getCurrentState() != GeneralStateE.SETUP) {
            throw new IllegalGameStateException("Game can only be started during SETUP state");
        }

        for (int i = 0; i < Players.MAX_HAND_SIZE; ++i) {
            for (Players p : players) {
                aDeck.drawCard(p.getHand());
            }
        }

        stateMachine.setGameStartRequested(true);
        stateMachine.update();
        if (stateMachine.getCurrentState() != GeneralStateE.SETUP) {
            outboundService.broadcastGameStart();
        }
    }


    @Override
    public boolean receiveCard(StoryCards card) {
        if (stateMachine.getCurrentState() != GeneralStateE.DRAW_STORY_CARD) {
            throw new IllegalGameStateException("Cannot receive story card when it's not DRAW_STORY_CARD state, currentState=" + stateMachine.getCurrentState());
        }

        discardCard(storyCard);
        storyCard = card;

        stateMachine.update();
        return true;
    }


    /**
     * Draw a story card from the story deck and play that card (generate the game phase)
     * @param players the player who requested
     */
    public void drawStoryCard(Players players) {
        if (stateMachine.getCurrentState() != GeneralStateE.DRAW_STORY_CARD) {
            throw new IllegalGameStateException("Story card can only be drawn during DRAW_STORY_CARD state");
        }

        if (playerTurnService.getPlayerTurn() != players) {
            return;
        }

        sDeck.drawCard(this);
        playCard(this.storyCard);
        playerTurnService.nextPlayer();

        stateMachine.update();
    }

    /**
     * Discard the story card to the discard pile
     * @param card
     */
    @Override
    public void discardCard(StoryCards card) {
        if (!stateMachine.isGameStarted()) {
            throw new IllegalGameStateException("Cannot receive card before the game is started");
        }

        // Ignore if there's no story card
        if (card != null) {
            card.discardCard();
        }

        stateMachine.update();
    }

    /**
     * Generate the appropriate game phase from the story card
     * @param card
     */
    @Override
    public boolean playCard(StoryCards card) {
        if (!stateMachine.isGameStarted()) {
            throw new IllegalGameStateException("Cannot play card before the game is started");
        } else if (card == null) {
            throw new NullPointerException("Story card is null");
        }

        switch (card.getSubType()) {
            case QUEST:
                questPhaseController.receiveCard(this.storyCard);
                questPhaseController.startPhase();
                break;
            case TOURNAMENT:
            case EVENT:
                break;
            default:
                throw new RuntimeException("Unexpected card type, should be a story card");
        }
        stateMachine.update();
        return true;
    }

    @Override
    public void onGameReset() {
        aDeck.onGameReset();
        sDeck.onGameReset();
        playerTurnService.onGameReset();
        questPhaseController.onGameReset();
        for (Players p : players) {
            p.onGameReset();
        }

        Collections.shuffle(players);
        winners.clear();

        storyCard = null;


        stateMachine.update();
    }

    // Only here to support backward dependency
    // TODO: Remove after Iteration 1 test
    public void dealCard(Players player) {
        aDeck.drawCard(player.getHand());
    }
}
