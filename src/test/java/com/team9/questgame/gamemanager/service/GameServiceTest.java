package com.team9.questgame.gamemanager.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GameServiceTest {

    @Autowired
    GameService gameService;

    @Autowired
    SessionService sessionService;

    @Test
    void contextLoad() {
        assertThat(gameService).isNotNull();
        assertThat(sessionService).isNotNull();
    }

    @Test
    void startGame() {
        // Not enough player
        assertFalse(gameService.startGame());
        assertFalse(gameService.isGameStarted());

        // Enough player
        sessionService.registerPlayer("A");
        sessionService.registerPlayer("B");
        assertTrue(gameService.startGame());
        assertTrue(gameService.isGameStarted());

        // Start the game the second time
        assertTrue(gameService.startGame());
        assertTrue(gameService.isGameStarted());

    }
}