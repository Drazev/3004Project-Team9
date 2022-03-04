package com.team9.questgame;

import com.team9.questgame.Data.PlayerRewardData;
import com.team9.questgame.Entities.PlayerRanks;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.AdventureDecks;
import com.team9.questgame.Entities.cards.CardArea;
import com.team9.questgame.Entities.cards.StoryCards;
import com.team9.questgame.Entities.cards.StoryDecks;
import com.team9.questgame.GamePhases.GamePhases;
import com.team9.questgame.exception.IllegalGameRequest;
import com.team9.questgame.exception.IllegalGameStateException;
import com.team9.questgame.exception.PlayerJoinException;
import com.team9.questgame.exception.PlayerNotFoundException;
import com.team9.questgame.gamemanager.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class QuestGameController implements CardArea<StoryCards> {
    private Logger LOG;
    private final PlayerRanks victoryCondtion;
    public static final int MAX_PLAYERS =4;
    public static final int MIN_PLAYERS=2;
    private ArrayList<Players> players;
    private ArrayList<Players> winners;
    private final GameService gameService;
    private GameStates currentState;
    private Players activePlayer;
    private StoryCards storyCard;
    private Iterator<Players> turnSequence;
    private GamePhases activePhase;

    private AdventureDecks aDeck;
    private StoryDecks sDeck;

    public QuestGameController(GameService gameService) {
        this(gameService,PlayerRanks.KNIGHT_OF_ROUND_TABLE);
    }

    public QuestGameController(GameService gameService,PlayerRanks victoryRank) {
        LOG = LoggerFactory.getLogger(QuestGameController.class);
        this.players = new ArrayList<>();
        this.gameService = gameService;
        this.currentState=GameStates.SETUP_OPEN;
        this.activePlayer =null;
        this.victoryCondtion=victoryRank;
        this.winners=new ArrayList<>();
        turnSequence=null;
        activePhase=null;
        aDeck = new AdventureDecks();
        sDeck = new StoryDecks();
    }

    public boolean startGame() {
        if(players.size()<MIN_PLAYERS) {
            return false;
        }
        else if(players.size()>MAX_PLAYERS) {
            LOG.error("Game Controller has too many players");
            throw new IllegalGameStateException("Game Controller has too many players. Players must be removed until there are at most "+MAX_PLAYERS+" players.");
        }

        onGameReset();

        runGame();

        return true;
    }

    /**
     * This function registers a new player to the game.
     *
     * It must be the ONLY way for a player to join the game
     * and acts as a gatekeeper enforcing the player limits.
     *
     * @param player The player wishing to join the game
     * @throws PlayerJoinException An error that is thrown when a player is unable to join the game. It includes a reason.
     */
    void playerJoin(Players player) throws PlayerJoinException {
        if(players.contains(player)) {
            return; //Duplicate join request
        }
        else if(currentState==GameStates.SETUP_FULL)
        {
            throw new PlayerJoinException(player, PlayerJoinException.PlayerJoinExceptionReasonCodes.GAME_FULL);
        }
        else if(currentState!=GameStates.SETUP_OPEN) {
            throw new PlayerJoinException(player, PlayerJoinException.PlayerJoinExceptionReasonCodes.GAME_IN_PROGRESS);
        }

        LOG.info("Player {name: "+player.getName()+", playerId: "+player.getPlayerId()+"} requested to join. Result: SUCCESS");
        players.add(player);

        if(players.size()>=MAX_PLAYERS) {
            currentState=GameStates.SETUP_FULL;
        }
    }

    void removePlayer(Players player) throws PlayerNotFoundException, IllegalGameRequest {
        if(!players.contains(player)) {
            throw new PlayerNotFoundException(player);
        }
        else if( !(currentState==GameStates.SETUP_FULL || currentState==GameStates.SETUP_OPEN) ) {
            throw new IllegalGameRequest("Players cannot be removed after game has been started.",player);
        }
        LOG.info("Player {name: "+player.getName()+", playerId: "+player.getPlayerId()+"} requested to disconnect. Result: SUCCESS");
        players.remove(player);

        //A past version allowed players to be passed via constructor.
        //This guard ensures that if the players registered exeeded the max players by more than one, the game state will still be full
        currentState = players.size()>=MAX_PLAYERS ? GameStates.SETUP_FULL : GameStates.SETUP_OPEN;
    }

    private void runGame() {
        currentState = GameStates.RUNNING;

        //Deal card to each player up to max hand size
        for(int i=0;i<Players.MAX_HAND_SIZE;++i) {
            for(Players p : players) {
                dealCard(p);
            }
        }

        //Main game loop
        while(currentState==GameStates.RUNNING) {
            nextPlayersTurn();

            //Draw Story Card
            sDeck.drawCard(this);

            playCard(storyCard); //set's quest phase
            activePhase.startPhase();

            //Play area discards. We do this here instead of phase since we always discard down to Allies regardless of phase
            endOfRoundDiscardPhase();

            PlayerRewardData awards=activePhase.getRewards();
            if(awards!=null) {
                issueRewards(awards);
            }

            discardCard(storyCard);

            //Check Victory Conditions
            if(checkVictoryConditions()) {
                //If victory condition, is there a tie?
                if(currentState==GameStates.FINISHED_CONTESTED)
                {
                    //If tie do special tournament sequence
                }

                //end of game things
            }
        }
    }

    private void nextPlayersTurn() {
        if(turnSequence==null || !turnSequence.hasNext()) {
            turnSequence=players.iterator();
        }

        activePlayer =turnSequence.next();
        //TODO:Notify client new turn has begun
    }

    @Override
    public void receiveCard(StoryCards card) {
        discardCard(storyCard);
        storyCard=card;
    }

    @Override
    public void discardCard(StoryCards card) {
        if(card!=null) {
            card.discardCard();
        }
    }

    /**
     * Set's the active gamePhase based on drawn card.
     *
     * @param card The story card drawn by the player
     */
    @Override
    public void playCard(StoryCards card) {
        activePhase=card.generateGamePhase(players,this);
        if(activePhase==null)
        {
            throw new IllegalGameStateException("Game Phase not generated by Story Card: "+card.getCardCode());
        }
    }

    @Override
    public void onGameReset() {
        aDeck.onGameReset();
        sDeck.onGameReset();
        for(Players p : players) {
            p.onGameReset();
        }

        Collections.shuffle(players);
        winners.clear();

        activePlayer =null;
        storyCard=null;
        turnSequence=null;

        currentState = players.size()>=MAX_PLAYERS ? GameStates.SETUP_FULL : GameStates.SETUP_OPEN;

    }

    private boolean checkVictoryConditions() {
        for(Players p : players) {
            if(p.getRank()==victoryCondtion) {
                winners.add(p);
            }
        }

        if(winners.size()<1) {
            return false;
        }
        else if(winners.size()>1) {
            currentState = GameStates.FINISHED_CONTESTED;
        }
        else {
            currentState=GameStates.FINISHED;
        }
        return true;
    }

    private void issueRewards(PlayerRewardData data) {

    }

    private void endOfRoundDiscardPhase() {

    }

    //TODO: Remove after Iteration 1 test
    public void dealCard(Players player)
    {
        aDeck.drawCard(player);
    }

}
