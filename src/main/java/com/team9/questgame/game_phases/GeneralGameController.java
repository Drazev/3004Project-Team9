package com.team9.questgame.game_phases;

import com.team9.questgame.Data.PlayerData;
import com.team9.questgame.Entities.PlayerRanks;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.*;
import com.team9.questgame.exception.IllegalGameRequest;
import com.team9.questgame.exception.IllegalGameStateException;
import com.team9.questgame.exception.PlayerJoinException;
import com.team9.questgame.exception.PlayerNotFoundException;
import com.team9.questgame.game_phases.event.EventPhaseController;
import com.team9.questgame.game_phases.quest.QuestPhaseController;
import com.team9.questgame.game_phases.tournament.TournamentPhaseController;
import com.team9.questgame.game_phases.utils.PlayerTurnService;
import com.team9.questgame.gamemanager.record.socket.NotificationOutbound;
import com.team9.questgame.gamemanager.record.socket.WinnerOutbound;
import com.team9.questgame.gamemanager.service.InboundService;
import com.team9.questgame.gamemanager.service.NotificationOutboundService;
import com.team9.questgame.gamemanager.service.OutboundService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;

@Service
public class GeneralGameController implements CardArea<StoryCards>, ApplicationContextAware {
    private ApplicationContext context;
    public static final int MAX_PLAYERS = 4;
    public static final int MIN_PLAYERS = 2;
    @Getter
    private final PlayerRanks victoryCondtion;
    private final Logger LOG;
    @Getter
    private final ArrayList<Players> players;
    @Getter
    private ArrayList<Players> winners;

    @Getter
    private final AdventureDecks aDeck;
    @Getter
    private final StoryDecks sDeck;

    @Getter
    private HashSet<CardTypes> allowedStoryCardTypes;

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
    private boolean isEndGameTournamentPlayed;

    @Getter
    private GamePhases currPhase;

    public GeneralGameController() {
        this(PlayerRanks.KNIGHT);
    }

    public GeneralGameController(PlayerRanks victoryRank) {
        LOG = LoggerFactory.getLogger(GeneralGameController.class);
        this.players = new ArrayList<>();
        this.victoryCondtion = victoryRank;
        this.winners = new ArrayList<>();
        this.currPhase=null;
        this.isEndGameTournamentPlayed=false;
        aDeck = new AdventureDecks();
        sDeck = new StoryDecks();
        this.playerTurnService = new PlayerTurnService(this.players);
        allowedStoryCardTypes = new HashSet<>();
        allowedStoryCardTypes.add(CardTypes.QUEST);
        allowedStoryCardTypes.add(CardTypes.EVENT);
        allowedStoryCardTypes.add(CardTypes.TOURNAMENT);
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
        } else if (stateMachine.isBlocked()) {
            throw new RuntimeException("The game should not be blocked before started");
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
        } else if (stateMachine.isBlocked()) {
            throw new RuntimeException("The game should not be blocked before started");
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
        } else if (stateMachine.isBlocked()) {
            throw new RuntimeException("The game should not be blocked before started");
        }
        onGameReset();
        for (int i = 0; i < Hand.MAX_HAND_SIZE; ++i) {
            for (Players p : players) {
                aDeck.drawCard(p.getHand());
            }
        }

        stateMachine.setGameStartRequested(true);
        stateMachine.update();
        if (stateMachine.getCurrentState() != GeneralStateE.SETUP) {
            playerTurnService.notifyTurnChange();
            outboundService.broadcastGameStart();
        }
    }

    /**
     * Draw a story card from the story deck and play that card (generate the game phase)
     *
     * @param player the player who requested
     */
    public void drawStoryCard(Players player) {
        if (stateMachine.getCurrentState() != GeneralStateE.DRAW_STORY_CARD) {
            LOG.error(String.format("general state: %s ", stateMachine.getCurrentState()));
            throw new IllegalGameStateException("Story card can only be drawn during DRAW_STORY_CARD state");
        } else if (stateMachine.isBlocked()) {
            throw new IllegalGameRequest("Cannot proceed when the game is blocked", player);
        }

        if (playerTurnService.getPlayerTurn() != player) {
            // TODO: Throw error because this is client request
            return;
        }

        sDeck.drawCard(this);

        // Temporarily here to only start Quest Phase
        while (!allowedStoryCardTypes.contains(this.storyCard.getSubType())) {
            this.discardCard(this.storyCard);
            sDeck.drawCard(this);
        }

        outboundService.broadcastStoryCard(this.storyCard.generateCardData());

        // Generate the GamePhase that corresponds to the type of story card
        boolean status = playCard(this.storyCard);
        if (status == false) {
            throw new RuntimeException("Couldn't play the StoryCard");
        }

        stateMachine.update();
    }

    public void handlePlayerHandOversize() {
        boolean isOversize = false;
//        if ( !( stateMachine.isInPhases() || stateMachine.getCurrentState()==GeneralStateE.PLAYER_HAND_OVERSIZE) ) {
//            throw new IllegalGameStateException("Player hand should only be oversize when " +
//                    "in QUEST_PHASE, EVENT_PHASE, or TOURNAMENT_PHASE or already in PLAYER_HAND_OVERSIZE state.\n" +
//                    "Current state: " + stateMachine.getCurrentState());
//        }

        // Double check
        for (Players p: this.players) {
            if (p.getHand().isHandOversize()) {
                isOversize = true;
            }
        }
        if (!isOversize) {
            throw new RuntimeException("Expecting at least one oversize hand");
        }

        stateMachine.setHandOversizeRequested(true);
        stateMachine.update();
    }


    public void playerPlayCard(Players player, long cardId) {
//        if (!getStateMachine().isInPhases() && !getStateMachine().isBlocked()) {
//            throw new IllegalGameStateException("Card can only be played in a Game Phases or when it's HAND_OVERSIZE");
//        }


        player.actionPlayCard(cardId);
        stateMachine.update();
    }

    public void playerDiscardCard(Players player, long cardId) {
        player.actionDiscardCard(cardId);
        stateMachine.update();
    }

    @Override
    public boolean receiveCard(StoryCards card) {
        if (stateMachine.getCurrentState() != GeneralStateE.DRAW_STORY_CARD) {
            // Cannot receive story card when it's not DRAW_STORY_CARD state, currentState=
            return false;
        } else if (stateMachine.isBlocked()) {
            // Cannot receive card when false
            return false;
        }

        if(storyCard!=null) {
            discardCard(storyCard);
        }
        storyCard = card;

        stateMachine.update();
        return true;
    }


    /**
     * Discard the story card to the discard pile
     * This should occur once a game phase has ended
     * @param card The card to be discarded
     */
    @Override
    public void discardCard(StoryCards card) {

        // Ignore if there's no story card
        if (card != null) {
            card.discardCard();
            this.storyCard = null;
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
        } else if (stateMachine.isBlocked()) {
            throw new RuntimeException("playCard should not be called when the game is blocked");
        }else if (card == null) {
            throw new NullPointerException("Story card is null");
        }

        PlayerTurnService gamePhaseTurnService = new PlayerTurnService(players);
        gamePhaseTurnService.setPlayerTurn(playerTurnService.getPlayerTurn());

        /**
         * Start the correct phase that corresponds to the type of StoryCard
         */
        switch (card.getSubType()) {
            case QUEST:
                currPhase = new QuestPhaseController(this,(QuestCards) card);
                break;
            case TOURNAMENT:
                currPhase = new TournamentPhaseController(this,(TournamentCards) card);
                break;
            case EVENT:
                currPhase = new EventPhaseController(this,(EventCards) card);
                break;
            default:
                throw new RuntimeException("Unexpected card type, should be a story card");
        }

        stateMachine.setGamePhaseRequested(true);
        stateMachine.update();

        currPhase.startPhase(gamePhaseTurnService);

        return true;
    }

    public void requestPhaseEnd(){
        discardCard(storyCard);
        currPhase=null;
        playerTurnService.nextPlayer();
        stateMachine.setPhaseEndRequested(true);
        stateMachine.update();
    }

    @Override
    public void onGameReset() {
        aDeck.onGameReset();
        sDeck.onGameReset();
        playerTurnService.onGameReset();
        for (Players p : players) {
            p.onGameReset();
        }

        // Collections.shuffle(players);
        this.playerTurnService = new PlayerTurnService(this.players);
        winners.clear();

        storyCard = null;
        this.isEndGameTournamentPlayed = false;

        stateMachine.setCurrentState(GeneralStateE.SETUP);
    }

    public Players findPlayerWithID(long playerID) {
        Players player=null;
        for(Players p : players) {
            if(p.getPlayerId()==playerID){
                player=p;
            }
        }
        return player;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context=applicationContext;
    }

    public GeneralGameController getService() {
        return context.getBean(GeneralGameController.class);
    }

    /**
     * Called everytime we go back to DRAW_STORY_CARD state to check if there's a winner
     */
    private void checkWinCondition() {

        // Update the winners array to reflect the new winners
        for (Players p : players) {
            if (p.getRank() == this.victoryCondtion && !winners.contains(p)) {
                this.winners.add(p);
            } else if (p.getRank() == this.victoryCondtion && winners.contains(p)) {
                this.winners.remove(p);
            }
        }

        stateMachine.update();
    }

    /**
     * Start the winner tournament if there's more than one winner, otherwise simply update the game state
     * to move to ENDED
     */
    private void startWinnerTournament() {
        if (stateMachine.getCurrentState() != GeneralStateE.DETERMINING_WINNER) {
            throw new IllegalGameStateException("Cannot start winner tournament in state " + stateMachine.getCurrentState());
        } else if (this.isEndGameTournamentPlayed) {
            throw new IllegalGameStateException("Cannot start winner tournament twice");
        }

        if (this.winners.size() == 0) {
            throw new RuntimeException("No winners found");
        } else if (this.winners.size() == 1) {
            // There's no need to start the winner tournament
        } else {
            // Start the winner tournament
            PlayerTurnService gamePhaseTurnService = new PlayerTurnService(players);
            gamePhaseTurnService.setPlayerTurn(playerTurnService.getPlayerTurn());
            currPhase = new TournamentPhaseController(this, this.winners);
            currPhase.startPhase(gamePhaseTurnService);

            NotificationOutbound notification = new NotificationOutbound(
                    "Starting the endgame tournament",
                    String.format("%d players have reached an impasse, a tournament will commence to determine the final victor", winners.size()),
                    null,
                    null);
            NotificationOutboundService.getService().sendInfoNotification( winners.get(0), notification, notification );
            stateMachine.update();
        }

        stateMachine.update();
    }

    public void requestWinnerTournamentEnd(ArrayList<Players> tournamentWinners) {
        this.isEndGameTournamentPlayed = true;
        this.winners = tournamentWinners;
        stateMachine.update();
    }

    private void endGame() {
        if (stateMachine.getCurrentState() != GeneralStateE.ENDED) {
            throw new IllegalGameStateException("Cannot end game in state " + stateMachine.getCurrentState());
        } else {
            if (this.winners.size() == 0) {
                throw new RuntimeException("Cannot end game with no winners");
            } else if (this.winners.size() > 1 && !this.isEndGameTournamentPlayed) {
                throw new RuntimeException("Cannot end game with %d winners without starting the winner tournament");
            }
        }

        for (Players winner : winners) {
            NotificationOutboundService.getService().sendGoodNotification(
                    winner,
                    new NotificationOutbound("Game Ended", "Congratulations, you won the game!", null, null),
                    new NotificationOutbound("Game Ended", String.format("%s won the game!", this.winners.get(0).getName()), null, null)
            );
        }

        ArrayList<PlayerData> winnerDatas = new ArrayList<>();
        for (Players winner : winners) {
            winnerDatas.add(winner.generatePlayerData());
        }
        OutboundService.getService().broadcastGameEnded(new WinnerOutbound(winnerDatas));

        stateMachine.update();
    }

    /**
     * Called by the GeneralStateMachine to execute action when state changes
     */
    public void executeNextAction() {
        switch (stateMachine.getCurrentState()) {
            case DRAW_STORY_CARD:
                this.checkWinCondition();
                break;
            case DETERMINING_WINNER:
                this.startWinnerTournament();
                break;
            case ENDED:
                this.endGame();
                break;
            default:
                break;
        }

    }
}
