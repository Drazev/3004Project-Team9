package com.team9.questgame.game_phases.tournament;

import com.team9.questgame.Entities.PlayerRanks;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.*;
import com.team9.questgame.exception.IllegalGameRequest;
import com.team9.questgame.game_phases.GeneralGameController;
import com.team9.questgame.game_phases.GeneralStateE;
import com.team9.questgame.game_phases.quest.QuestPhaseController;
import com.team9.questgame.game_phases.utils.PlayerTurnService;
import com.team9.questgame.gamemanager.record.socket.PlayerPlayCardInbound;
import com.team9.questgame.gamemanager.service.InboundService;
import com.team9.questgame.gamemanager.service.SessionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TournamentPhaseControllerTest {

    private TournamentPhaseController controller;

    @Autowired
    GeneralGameController game;

    @Autowired
    InboundService inboundService;

    @Autowired
    SessionService session;

    ArrayList<TournamentCards> tournamentCards;

    private ArrayList<Players> players;
    private PlayerTurnService turnService;

    @BeforeEach
    void setUp(){
        //tournamentCards=getTournamentCards();

        players = new ArrayList<>();
        session.registerPlayer("Player 1");
        session.registerPlayer("Player 2");
        session.registerPlayer("Player 3");
        session.registerPlayer("Player 4");
        InboundService.getService().startGame();
        turnService=game.getPlayerTurnService();
        players.addAll(game.getPlayerTurnService().getPlayers());
        game.getAllowedStoryCardTypes().clear();
        game.getAllowedStoryCardTypes().add(CardTypes.TOURNAMENT);
        game.drawStoryCard(turnService.getPlayerTurn());
        controller = (TournamentPhaseController) game.getCurrPhase();
//        game.getStateMachine().setCurrentState(GeneralStateE.QUEST_PHASE);

//        generalGameController.drawStoryCard(generalGameController.getPlayerTurnService().getPlayerTurn());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void checkJoinResult() { // tests all join conditions
        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.JOIN);

        int rewards = controller.getCard().getBonusShields();
        //Can't join twice
        controller.checkJoinResult(players.get(0), true);
        assertThrows(IllegalGameRequest.class,
                () -> controller.checkJoinResult(players.get(0), true));
        assertThrows(IllegalGameRequest.class,
                () -> controller.checkJoinResult(players.get(0), false));

        //if only one player joins the tournament ends and they win
        controller.checkJoinResult(players.get(1), false);
        controller.checkJoinResult(players.get(2), false);
        controller.checkJoinResult(players.get(3), false);

        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.READY);
        assertThat(game.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);
        assertThat(players.get(0).getShields()).isEqualTo(rewards+1);
        assertThat(players.get(1).getShields()).isEqualTo(0);
        assertThat(players.get(2).getShields()).isEqualTo(0);
        assertThat(players.get(3).getShields()).isEqualTo(0);

        //draw another tournament and continue
        game.drawStoryCard(turnService.getPlayerTurn());
        controller = (TournamentPhaseController) game.getCurrPhase();

        //if no one joins, tournament ends and no rewards are distributed
        controller.checkJoinResult(players.get(0), false);
        controller.checkJoinResult(players.get(1), false);
        controller.checkJoinResult(players.get(2), false);
        controller.checkJoinResult(players.get(3), false);

        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.READY);
        assertThat(game.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);
        assertThat(players.get(0).getShields()).isEqualTo(rewards+1);
        assertThat(players.get(1).getShields()).isEqualTo(0);
        assertThat(players.get(2).getShields()).isEqualTo(0);
        assertThat(players.get(3).getShields()).isEqualTo(0);

        //draw another tournament and continue
        game.drawStoryCard(turnService.getPlayerTurn());
        controller = (TournamentPhaseController) game.getCurrPhase();

        //if everyone joins, deal cards and become blocked
        controller.checkJoinResult(players.get(0), true);
        controller.checkJoinResult(players.get(1), true);
        controller.checkJoinResult(players.get(2), true);
        controller.checkJoinResult(players.get(3), true);

        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.BLOCKED);
        assertThat(game.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.PLAYER_HAND_OVERSIZE);

    }
    @Test
    void TwoPlayerTie() { // test two player tie with one winner
        assertThat(controller.getCard()).isNotNull();
        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.JOIN);
        int rewards = controller.getCard().getBonusShields();

        //have two players join
        controller.checkJoinResult(players.get(0), true);
        controller.checkJoinResult(players.get(1), true);
        controller.checkJoinResult(players.get(2), false);
        controller.checkJoinResult(players.get(3), false);
        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.BLOCKED);

        inboundService.playerDiscardCard(players.get(0).getName(), players.get(0).getHand().getHand().iterator().next().getCardID());
        inboundService.playerDiscardCard(players.get(1).getName(), players.get(1).getHand().getHand().iterator().next().getCardID());

        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.PLAYER_SETUP);

        //check that one participant finishing setup keeps correct state
        inboundService.tournamentCompetitorSetup(players.get(0).getName());
        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.PLAYER_SETUP);

        //check that non-participant setup complete throws an error
        assertThrows(IllegalGameRequest.class,
                () -> inboundService.tournamentCompetitorSetup(players.get(3).getName()));
        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.PLAYER_SETUP);

        //check that second participant completing setup is caught as a tie and tiebreaker flag is activated
        inboundService.tournamentCompetitorSetup(players.get(1).getName());
        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.PLAYER_SETUP);
        assertThat(controller.isTiebreaker()).isTrue();

        //finish have both participants submit again should end with both winning
        inboundService.tournamentCompetitorSetup(players.get(0).getName());
        Iterator<AdventureCards> hand =  players.get(1).getHand().getHand().iterator();
        AdventureCards card =hand.next();
        while(card.getSubType() != CardTypes.AMOUR && card.getSubType() != CardTypes.WEAPON){
            if(!hand.hasNext()){
                break;
            }
            card =  hand.next();
        }
        inboundService.playerPlayCard(new PlayerPlayCardInbound(players.get(1).getName(), card.getCardID(), players.get(1).getPlayerId()));
        //check that second participant completing setup leads to second player winning
        inboundService.tournamentCompetitorSetup(players.get(1).getName());
        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.READY);
        assertThat(players.get(0).getShields()).isEqualTo(0);

        //if the tournament had 3 bonus shields then the winner is a knight, so shields reset
        if(players.get(1).getRank() == PlayerRanks.KNIGHT) {
            assertThat(players.get(1).getShields()).isEqualTo(0);
        }
        else {//otherwise they have shields = bonus shields + participants
            assertThat(players.get(1).getShields()).isEqualTo(rewards + 2);
        }

    }

    @Test
    void TwoPlayerTie2() { //test two player tie with both winning
        assertThat(controller.getCard()).isNotNull();
        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.JOIN);
        int rewards = controller.getCard().getBonusShields();

        //have two players join
        controller.checkJoinResult(players.get(0), true);
        controller.checkJoinResult(players.get(1), true);
        controller.checkJoinResult(players.get(2), false);
        controller.checkJoinResult(players.get(3), false);
        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.BLOCKED);

        inboundService.playerDiscardCard(players.get(0).getName(), players.get(0).getHand().getHand().iterator().next().getCardID());
        inboundService.playerDiscardCard(players.get(1).getName(), players.get(1).getHand().getHand().iterator().next().getCardID());

        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.PLAYER_SETUP);

        //check that one participant finishing setup keeps correct state
        inboundService.tournamentCompetitorSetup(players.get(0).getName());
        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.PLAYER_SETUP);

        //check that non-participant setup complete throws an error
        assertThrows(IllegalGameRequest.class,
                () -> inboundService.tournamentCompetitorSetup(players.get(3).getName()));
        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.PLAYER_SETUP);

        //check that second participant completing setup is caught as a tie and tiebreaker flag is activated
        inboundService.tournamentCompetitorSetup(players.get(1).getName());
        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.PLAYER_SETUP);
        assertThat(controller.isTiebreaker()).isTrue();

        //finish have both participants submit again should end with both winning
        inboundService.tournamentCompetitorSetup(players.get(0).getName());
        inboundService.tournamentCompetitorSetup(players.get(1).getName());
        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.READY);
        assertThat(players.get(0).getShields()).isEqualTo(rewards+2);
        assertThat(players.get(1).getShields()).isEqualTo(rewards+2);


    }

    @Test
    void oneWinner() {//test full tourney with one winner
        assertThat(controller.getCard()).isNotNull();
        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.JOIN);
        int rewards = controller.getCard().getBonusShields();

        //have two players join
        controller.checkJoinResult(players.get(0), true);
        controller.checkJoinResult(players.get(1), true);
        controller.checkJoinResult(players.get(2), true);
        controller.checkJoinResult(players.get(3), true);
        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.BLOCKED);

        inboundService.playerDiscardCard(players.get(0).getName(), players.get(0).getHand().getHand().iterator().next().getCardID());
        inboundService.playerDiscardCard(players.get(1).getName(), players.get(1).getHand().getHand().iterator().next().getCardID());
        inboundService.playerDiscardCard(players.get(2).getName(), players.get(2).getHand().getHand().iterator().next().getCardID());
        inboundService.playerDiscardCard(players.get(3).getName(), players.get(3).getHand().getHand().iterator().next().getCardID());

        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.PLAYER_SETUP);

        inboundService.tournamentCompetitorSetup(players.get(0).getName());
        inboundService.tournamentCompetitorSetup(players.get(2).getName());
        inboundService.tournamentCompetitorSetup(players.get(3).getName());


        Iterator<AdventureCards> hand =  players.get(1).getHand().getHand().iterator();
        AdventureCards card =hand.next();
        while(card.getSubType() != CardTypes.AMOUR && card.getSubType() != CardTypes.WEAPON){
            if(!hand.hasNext()){
                break;
            }
            card =  hand.next();
        }
        inboundService.playerPlayCard(new PlayerPlayCardInbound(players.get(1).getName(), card.getCardID(), players.get(1).getPlayerId()));
        //check that second participant completing setup leads to second player winning
        inboundService.tournamentCompetitorSetup(players.get(1).getName());
        assertThat(controller.getCurrState()).isEqualTo(TournamentPhaseStatesE.READY);
        assertThat(players.get(0).getShields()).isEqualTo(0);
        assertThat(players.get(2).getShields()).isEqualTo(0);
        assertThat(players.get(3).getShields()).isEqualTo(0);


        //if the tournament had 3 bonus shields then the winner is a knight, so shields reset
        if(players.get(1).getRank() == PlayerRanks.KNIGHT) {
            assertThat(players.get(1).getShields()).isEqualTo(rewards-1);
        }
        else {//otherwise they have shields = bonus shields + participants
            assertThat(players.get(1).getShields()).isEqualTo(rewards + 4);
        }
    }



    private ArrayList<TournamentCards> getTournamentCards() {
        CardFactory cf = CardFactory.getInstance();
        AdventureDecks testDeck = new AdventureDecks();
        HashMap<StoryDeckCards,Integer> deckList = new HashMap<>();
        ArrayList<TournamentCards> cards = new ArrayList<>();
        deckList.put(StoryDeckCards.TOURNAMENT_AT_CAMELOT,1);
        deckList.put(StoryDeckCards.TOURNAMENT_AT_ORKNEY, 1);
        deckList.put(StoryDeckCards.TOURNAMENT_AT_YORK, 1);
        deckList.put(StoryDeckCards.TOURNAMENT_AT_TINTAGEL, 1);

        for(Map.Entry<StoryDeckCards,Integer> e : deckList.entrySet()) {

            //Create number of cards as proscribed in list
            for (int i = 0; i < e.getValue(); ++i) {
                TournamentCards card = (TournamentCards) cf.createCard(testDeck, e.getKey());
                cards.add(card);
            }
        }
        return cards;
    }
}