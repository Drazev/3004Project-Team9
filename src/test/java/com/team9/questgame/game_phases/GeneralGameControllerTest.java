package com.team9.questgame.game_phases;

import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.StoryCards;
import com.team9.questgame.exception.IllegalGameStateException;
import com.team9.questgame.exception.PlayerJoinException;
import com.team9.questgame.exception.PlayerNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;

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

        assertThrows(IllegalGameStateException.class, () -> gameController.drawStoryCard());

        // Allowed receiving card
        for (int i = 0; i < GeneralGameController.MAX_PLAYERS; ++i) {
            gameController.playerJoin(players.get(i));
        }
        gameController.startGame();
        assertThat(gameController.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);

        gameController.drawStoryCard();
        assertThat(gameController.getStoryCard()).isInstanceOf(StoryCards.class);

    }

    @Test
    void playCard() {
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

    @Test
    void getPlayers() {
    }

    @Test
    void getADeck() {
    }

    @Test
    void getSDeck() {
    }
}