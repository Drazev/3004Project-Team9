package com.team9.questgame.game_phases;

import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.*;
import com.team9.questgame.exception.IllegalGameStateException;
import com.team9.questgame.exception.PlayerJoinException;
import com.team9.questgame.exception.PlayerNotFoundException;
import com.team9.questgame.game_phases.quest.QuestPhaseStatesE;
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

    private ArrayList<Players> players;

    @BeforeEach
    void setUp() {
        players = new ArrayList<>();
        players.add(new Players("Player 1"));
        players.add(new Players("Player 2"));
        players.add(new Players("Player 3"));
        players.add(new Players("Player 4"));
        players.add(new Players("Player 5"));
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
        for (int i = 0; i < GeneralGameController.MAX_PLAYERS; ++i) {
            gameController.playerJoin(players.get(i));
        }
        assertThat(gameController.getPlayers().size()).isEqualTo(GeneralGameController.MAX_PLAYERS);

        // Should not add the same player
        assertThrows(PlayerJoinException.class, () -> gameController.playerJoin(players.get(0)));
        assertThat(gameController.getPlayers().size()).isEqualTo(GeneralGameController.MAX_PLAYERS);

        // Should not add more than MAX player
        assertThrows(PlayerJoinException.class, () -> gameController.playerJoin(players.get(4)));
        assertThat(gameController.getPlayers().size()).isEqualTo(GeneralGameController.MAX_PLAYERS);
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.SETUP);

        // Enroll player not in setup state
        gameController.startGame();

        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);
        assertThrows(IllegalGameStateException.class, () -> gameController.playerJoin(players.get(0)));
    }

    @Test
    void removePlayer() {
        for (int i = 0; i < GeneralGameController.MAX_PLAYERS; ++i) {
            gameController.playerJoin(players.get(i));
        }

        // Player not found
        assertThrows(PlayerNotFoundException.class, () -> gameController.removePlayer(players.get(4)));

        // Successful removal
        gameController.removePlayer(players.get(0));
        gameController.removePlayer(players.get(1));
        gameController.removePlayer(players.get(2));
        gameController.removePlayer(players.get(3));
        assertThat(gameController.getPlayers().size()).isEqualTo(0);

        // No player can be found
        assertThrows(PlayerNotFoundException.class, () -> gameController.removePlayer(players.get(0)));
        for (int i = 0; i < GeneralGameController.MAX_PLAYERS; ++i) {
            gameController.playerJoin(players.get(i));
        }

        // Remove player not in SETUP stage
        gameController.startGame();
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);
        assertThrows(IllegalGameStateException.class, () -> gameController.removePlayer(players.get(0)));
    }

    @Test
    void startGame() {
        // No state change when not enough player
        assertThat(gameController.getPlayers().size()).isEqualTo(0);
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.SETUP);
        gameController.startGame();
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.SETUP);

        // Can start the game
        for (int i = 0; i < GeneralGameController.MAX_PLAYERS; ++i) {
            gameController.playerJoin(players.get(i));
        }
        assertThat(gameController.getPlayers().size()).isEqualTo(GeneralGameController.MAX_PLAYERS);
        gameController.startGame();
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);

        // Cannot start the game when not in SETUP state
        assertThrows(IllegalGameStateException.class, () -> gameController.startGame());
        assertThrows(IllegalGameStateException.class, () -> gameController.startGame());
        assertThrows(IllegalGameStateException.class, () -> gameController.startGame());
    }

    @Test
    void receiveCard() {
        // Cannot receive card when the game is still being set up
        assertThat(gameController.getPlayers().size()).isEqualTo(0);
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.SETUP);

        assertThrows(IllegalGameStateException.class, () -> gameController.receiveCard(null));

        // Allowed receiving card
        for (int i = 0; i < GeneralGameController.MAX_PLAYERS; ++i) {
            gameController.playerJoin(players.get(i));
        }
        gameController.startGame();
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);

        // This function calls receiveCard in the process
        gameController.getSDeck().drawCard(gameController);
        assertThat(gameController.getStoryCard()).isInstanceOf(StoryCards.class);
    }

    @Test
    void discardCard() {
        // Cannot discard when the game is still being set up
        assertThat(gameController.getPlayers().size()).isEqualTo(0);
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.SETUP);

        assertThrows(IllegalGameStateException.class, () -> gameController.discardCard(null)); // input param doesn't matter

        // Allow discarding card
        for (int i = 0; i < GeneralGameController.MAX_PLAYERS; ++i) {
            gameController.playerJoin(players.get(i));
        }
        gameController.startGame();
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);
        gameController.getSDeck().drawCard(gameController);
        assertThat(gameController.getSDeck().getDiscardPile().size()).isEqualTo(0);
        gameController.discardCard(gameController.getStoryCard());
        assertThat(gameController.getSDeck().getDiscardPile().size()).isEqualTo(1);
    }

    @Test
    void drawStoryCard() {
        // Cannot draw card when the game not in DRAW_STORY_CARD state
        assertThat(gameController.getPlayers().size()).isEqualTo(0);
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.SETUP);

        assertThrows(IllegalGameStateException.class, () -> gameController.drawStoryCard(null)); // param doesn't matter here

        // Allowed drawing story card
        for (int i = 0; i < GeneralGameController.MAX_PLAYERS; ++i) {
            gameController.playerJoin(players.get(i));
        }
        gameController.startGame();
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
        // Not allowed in NOT_STARTED, SETUP, DRAW_STORY_CARD
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.SETUP);
        assertThrows(IllegalGameStateException.class, () -> gameController.handlePlayerHandOversize()); // Params doesn't matter

        // Start the game
        for (int i = 0; i < GeneralGameController.MAX_PLAYERS; ++i) {
            gameController.playerJoin(players.get(i));
        }
        gameController.startGame();
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);
        assertThrows(IllegalGameStateException.class, () -> gameController.handlePlayerHandOversize()); // Params doesn't matter

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
        AdventureCards lastDrawnCard = null;
        while (player.getHand().getHandSize() <= Hand.MAX_HAND_SIZE) {
            lastDrawnCard = gameController.getADeck().drawCard(player.getHand());
        }
        assertThat(lastDrawnCard).isNotNull();
        assertThat(player.getHand().getHandSize()).isEqualTo(Hand.MAX_HAND_SIZE + 1);

        gameController.getStateMachine().update();
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.PLAYER_HAND_OVERSIZE);
        assertThat(player.getHand().getHandSize()).isEqualTo(Hand.MAX_HAND_SIZE + 1);

        // Discard a card, the state should be reset to what was before
        gameController.playerDiscardCard(player, lastDrawnCard.getCardID());
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(previousState);
    }

    @Test
    void playCard() {
        // Cannot draw card when the game not in DRAW_STORY_CARD state
        assertThat(gameController.getPlayers().size()).isEqualTo(0);
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.SETUP);

        assertThrows(IllegalGameStateException.class, () -> gameController.playCard(null)); // param doesn't matter here

        // Allowed playing card
        for (int i = 0; i < GeneralGameController.MAX_PLAYERS; ++i) {
            gameController.playerJoin(players.get(i));
        }
        gameController.startGame();
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);

        // Get a quest card and generate the quest phase
        ArrayList<StoryCards> questCards = getQuestCards();
        gameController.receiveCard(questCards.get(0));
        gameController.playCard(gameController.getStoryCard());
        assertThat(gameController.getQuestPhaseController().getStoryCard()).isNotNull();
        assertThat(gameController.getQuestPhaseController().getStoryCard()).isEqualTo(gameController.getStoryCard());
        assertThat(gameController.getQuestPhaseController().getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_SPONSOR);

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