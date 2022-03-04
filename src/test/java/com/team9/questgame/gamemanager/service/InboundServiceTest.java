package com.team9.questgame.gamemanager.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class InboundServiceTest {

    @Autowired
    InboundService inboundService;

    @Autowired
    SessionService sessionService;

    @Test
    void contextLoad() {
        assertThat(inboundService).isNotNull();
        assertThat(sessionService).isNotNull();
    }

    @Test
    void startGame() {
        // Not enough player
        assertFalse(inboundService.startGame());
        assertFalse(inboundService.isGameStarted());

        // Enough player
        sessionService.registerPlayer("A");
        sessionService.registerPlayer("B");
        assertTrue(inboundService.startGame());
        assertTrue(inboundService.isGameStarted());

        // Start the game the second time
        assertTrue(inboundService.startGame());
        assertTrue(inboundService.isGameStarted());

    }
}