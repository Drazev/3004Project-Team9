package com.team9.questgame;

import com.team9.questgame.Entities.Players;
import com.team9.questgame.exception.PlayerJoinException;
import com.team9.questgame.gamemanager.controller.GameRestController;
import com.team9.questgame.gamemanager.service.GameService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QuestGameControllerTest {
    QuestGameController game;
    ArrayList<Players> players;

    @Autowired
    GameService gameService;
    Logger LOG;

    @BeforeEach
    void setUp() {
        game = new QuestGameController(gameService);
        LOG = LoggerFactory.getLogger(QuestGameControllerTest.class);
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

    @Test
    void startGame() {
    }

    @Test
    void playerJoin() {
        for(int i=0;i<game.MAX_PLAYERS;++i) {
            game.playerJoin(players.get(i));
        }

        game.playerJoin(players.get(0)); //Should not add any new players and return nothing.

        PlayerJoinException error = null;
        try {
            game.playerJoin(players.get(4)); //This should trigger a PlayerJoinException
        }
        catch (PlayerJoinException e){
            error=e;
        }
        finally {
            assert(error!=null);
        }

        //TODO: Start Game then try to add a player. It should produce an error
    }

    @Test
    void removePlayer() {
    }

    @Test
    void receiveCard() {
    }

    @Test
    void discardCard() {
    }

    @Test
    void playCard() {
    }

    @Test
    void onGameReset() {
    }
}