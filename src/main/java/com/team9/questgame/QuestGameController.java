package com.team9.questgame;

import com.team9.questgame.Data.PlayerRewardData;
import com.team9.questgame.Entities.PlayerRanks;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.AdventureDecks;
import com.team9.questgame.Entities.cards.CardArea;
import com.team9.questgame.Entities.cards.StoryCards;
import com.team9.questgame.Entities.cards.StoryDecks;
import com.team9.questgame.exception.IllegalGameRequest;
import com.team9.questgame.exception.IllegalGameStateException;
import com.team9.questgame.exception.PlayerJoinException;
import com.team9.questgame.exception.PlayerNotFoundException;
import com.team9.questgame.gamemanager.controller.GameRestController;
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
    private Players currPlayer;
    private StoryCards storyCard;
    private Iterator<Players> turnSequence;

    @Autowired
    private AdventureDecks aDeck;

    @Autowired
    private StoryDecks sDeck;

    public QuestGameController(GameService gameService) {
        this(new ArrayList<>(),gameService,PlayerRanks.KNIGHT_OF_ROUND_TABLE);
    }

    public QuestGameController(ArrayList<Players> players, GameService gameService,PlayerRanks victoryRank) {
        LOG = LoggerFactory.getLogger(QuestGameController.class);
        this.players = players;
        this.gameService = gameService;
        this.currentState=GameStates.SETUP_OPEN;
        this.currPlayer=null;
        this.victoryCondtion=victoryRank;
        this.winners=new ArrayList<>();
        turnSequence=null;
    }

    public boolean startGame() {
        if(players.size()<MIN_PLAYERS) {
            return false;
        }
        else if(players.size()>MAX_PLAYERS) {
            LOG.error("Game Controller has too many players");
            throw new IllegalGameStateException("Game Controller has too many players. Players must be removed until there are at most "+MAX_PLAYERS+" players.");
        }

        aDeck.onGameReset();
        sDeck.onGameReset();
        for(Players p : players) {
            p.onGameReset();
        }

        Collections.shuffle(players);
        winners.clear();

        runGame();

        return true;
    }


    void playerJoin(Players player) throws PlayerJoinException {
        if(players.contains(player)) {
            return; //Duplicate join request
        }
        else if(currentState!=GameStates.SETUP_OPEN) {
            throw new PlayerJoinException("Declined->Incorrect Game State",player);
        }
        else if(players.size()>=MAX_PLAYERS)
        {
            throw new PlayerJoinException("Declined->Game Full",player);
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
        currentState= GameStates.SETUP_OPEN;
    }

    private void runGame() {
        currentState = GameStates.RUNNING;

        //Main game loop
        while(currentState==GameStates.RUNNING) {
            nextPlayersTurn();

            //Draw Story Card
            sDeck.drawCard(this);
            PlayerRewardData awards=null; //returned from Game Phase once its done

            switch(storyCard.getSubType()) {

                case QUEST :
                    LOG.info("Quest Card Drawn");
                    break;
                case TEST:
                    LOG.info("Test Card Drawn");

                    break;

                case TOURNAMENT:
                    LOG.info("Tournament Card Drawn");

                    break;

                case EVENT:
                    LOG.info("Event Card Drawn");

                    break;

                default:
                    throw new IllegalGameStateException();
            }

            //Play area discards
            endOfRoundDiscardPhase();

            if(awards!=null) {
                issueRewards(awards);
            }

            //Check Victory Conditions
            discardCard(storyCard);
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

        currPlayer=turnSequence.next();
        //TODO:Notify client new turn has begun
    }

    @Override
    public void receiveCard(StoryCards card) {
        discardCard(storyCard);
        storyCard=card;
    }

    @Override
    public void discardCard(StoryCards card) {
        card.discardCard();
    }

    @Override
    public void playCard(StoryCards card) {

    }

    @Override
    public void onGameReset() {

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


}
