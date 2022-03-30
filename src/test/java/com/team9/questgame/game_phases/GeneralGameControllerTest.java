package com.team9.questgame.game_phases;

import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.*;
import com.team9.questgame.exception.IllegalGameStateException;
import com.team9.questgame.exception.PlayerJoinException;
import com.team9.questgame.exception.PlayerNotFoundException;
import com.team9.questgame.game_phases.quest.QuestPhaseStatesE;
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
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GeneralGameControllerTest {

    @Autowired
    private GeneralGameController gameController;
    @Autowired
    SessionService session;

    private ArrayList<Players> players;

    @BeforeEach
    void setUp() {
        players = new ArrayList<>();
        session.registerPlayer("Player 1");
        session.registerPlayer("Player 2");
        session.registerPlayer("Player 3");
        session.registerPlayer("Player 4");
        players.addAll(session.getPlayerMap().values());
        gameController.setShuffleOn(false);
        InboundService.getService().startGame();
    }

    @AfterEach
    void tearDown() {
    }

    @Autowired
    void contextLoad() {
        assertThat(gameController).isNotNull();

    }

    @Test
    void playerJoin() {
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);
        assertThrows(IllegalGameStateException.class, () -> gameController.playerJoin(players.get(0)));
    }

    @Test
    void removePlayer() {
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);
        assertThrows(IllegalGameStateException.class, () -> gameController.removePlayer(players.get(0)));
    }

    @Test
    void receiveCard() {
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);

        // This function calls receiveCard in the process
        gameController.getSDeck().drawCard(gameController);
        assertThat(gameController.getStoryCard()).isInstanceOf(StoryCards.class);
    }

    @Test
    void playerPlayCard() {
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);
        assertThrows(IllegalGameStateException.class, () -> gameController.playerPlayCard(null, 0)); // input param doesn't matter

        // Start a phase and attempt to play a card
        // TODO: Test other Game Phases other than Quest when they are implemented
        ArrayList<StoryCards> questCards = getQuestCards();
        gameController.receiveCard(questCards.get(0));
        gameController.playCard(gameController.getStoryCard());
        assertThat(gameController.getCurrPhase().getCard()).isNotNull();
//        assertThat(gameController.getCurrPhase().get.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_SPONSOR);

        // TODO: Fix Hand's playerPlayCard behaviour
//        Players player = gameController.getPlayers().get(0);
//        gameController.playerPlayCard(player, player.getHand().getHand().iterator().next().getCardID());


    }

    @Test
    void discardCard() {
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);
        gameController.getSDeck().drawCard(gameController);
        assertThat(gameController.getSDeck().getDiscardPile().size()).isEqualTo(0);
        gameController.discardCard(gameController.getStoryCard());
        assertThat(gameController.getSDeck().getDiscardPile().size()).isEqualTo(1);
    }

    @Test
    void drawStoryCard() {
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);

        // wrong player turn
        gameController.drawStoryCard(players.get(1));
        assertThat(gameController.getStoryCard()).isNull();


        // correct player turn
        gameController.drawStoryCard(gameController.getPlayerTurnService().getPlayerTurn());
        assertThat(gameController.getStoryCard()).isNotNull();
        assertThat(gameController.getStoryCard()).isInstanceOf(StoryCards.class);

    }

    @Test
    void handlePlayerHandOversize() {
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);
        assertThrows(RuntimeException.class, () -> gameController.handlePlayerHandOversize()); // Params doesn't matter

        gameController.drawStoryCard(gameController.getPlayerTurnService().getPlayerTurn());

        GeneralStateE currentState = gameController.getStateMachine().getCurrentState();
        assert(currentState == GeneralStateE.QUEST_PHASE
            || currentState == GeneralStateE.TOURNAMENT_PHASE
            || currentState == GeneralStateE.EVENT_PHASE);

        // Try to false trigger handOversize state when no hand is oversize
        assertThrows(RuntimeException.class, () -> gameController.handlePlayerHandOversize());

        // Make a hand go oversize
        Players player = gameController.getPlayers().get(0);
        GeneralStateE previousState = gameController.getStateMachine().getCurrentState();
        Enum previousQuestState = gameController.getCurrPhase().getCurrState();
        AdventureCards lastDrawnCard = null;
        while (player.getHand().getHandSize() <= Hand.MAX_HAND_SIZE) {
            lastDrawnCard = gameController.getADeck().drawCard(player.getHand());
        }
        assertThat(lastDrawnCard).isNotNull();
        assertThat(player.getHand().getHandSize()).isEqualTo(Hand.MAX_HAND_SIZE + 1);

        // TODO: Check if tournament is blocked as well
        gameController.getStateMachine().update();
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.PLAYER_HAND_OVERSIZE);
        assertThat(gameController.getCurrPhase().getCurrState()==QuestPhaseStatesE.BLOCKED);
        assertThat(player.getHand().getHandSize()).isEqualTo(Hand.MAX_HAND_SIZE + 1);

        // Discard a card, the state should be reset to what was before
        gameController.playerDiscardCard(player, lastDrawnCard.getCardID());
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(previousState);
        assertThat(gameController.getCurrPhase().getCurrState()==previousQuestState);
    }

    @Test
    void playCard() {
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);

        // Get a quest card and generate the quest phase
        ArrayList<StoryCards> questCards = getQuestCards();
        gameController.receiveCard(questCards.get(0));
        gameController.playCard(gameController.getStoryCard());
        assertThat(gameController.getCurrPhase().getCard()).isNotNull();
        assertThat(gameController.getCurrPhase().getCard().equals(gameController.getStoryCard()));
        assertThat(gameController.getCurrPhase().getCurrState()==QuestPhaseStatesE.QUEST_SPONSOR);

    }

    @Test
    void onGameReset() {
    }

    @Test
    void dealCard() {
    }

    @Test
    void getVictoryCondtion() {
    }

    ArrayList<StoryCards> getQuestCards() {
        CardFactory cf = CardFactory.getInstance();
        AdventureDecks testDeck = new AdventureDecks();
        HashMap<StoryDeckCards,Integer> deckList = new HashMap<>();
        ArrayList<StoryCards> cards = new ArrayList<>();
        deckList.put(StoryDeckCards.SEARCH_FOR_THE_HOLY_GRAIL,1);
        deckList.put(StoryDeckCards.TEST_OF_THE_GREEN_KNIGHT,1);
        deckList.put(StoryDeckCards.SEARCH_FOR_THE_QUESTING_BEAST,1);
        deckList.put(StoryDeckCards.DEFEND_THE_QUEENS_HONOR,1);
        deckList.put(StoryDeckCards.RESCUE_THE_FAIR_MAIDEN,1);
        deckList.put(StoryDeckCards.JOURNEY_THROUGH_THE_ENCHANTED_FOREST,1);
        deckList.put(StoryDeckCards.VANQUISH_KING_ARTHURS_ENEMIES,2);
        deckList.put(StoryDeckCards.SLAY_THE_DRAGON,1);
        deckList.put(StoryDeckCards.BOAR_HUNT,2);
        deckList.put(StoryDeckCards.REPEL_THE_SAXON_RAIDERS,2);

        for(Map.Entry<StoryDeckCards,Integer> e : deckList.entrySet()) {

            //Create number of cards as proscribed in list
            for (int i = 0; i < e.getValue(); ++i) {
                StoryCards card = cf.createCard(testDeck, e.getKey());
                cards.add(card);
            }
        }
        return cards;
    }
}