package com.team9.questgame.gamemanager.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SessionServiceTest {

    @Autowired
    SessionService sessionService;

    @Autowired
    OutboundService outboundService;

    @Test
    void contextLoad() {
        assertThat(sessionService).isNotNull();
        assertThat(outboundService).isNotNull();
    }

    @Test
    void registerPlayer() {
        int repetitions = 100;
        //sessionService = new SessionService();

        for (int i = 0; i < repetitions; i++) {
            assertTrue(sessionService.registerPlayer(Integer.toString(i)));
        }
        assertEquals(sessionService.getSessionMap().size(), repetitions);

        for (int i = 0; i < repetitions; i++) {
            assertFalse(sessionService.registerPlayer(Integer.toString(i)));
        }
        assertEquals(sessionService.getSessionMap().size(), repetitions);

    }

    @Test
    void deregisterPlayer() {
        int repetitions = 100;
        sessionService = prepareSession();

        for (int i = 0; i < repetitions; i++) {
            assertFalse(sessionService.deregisterPlayer("lol" + i));
        }
        assertEquals(sessionService.getSessionMap().size(), repetitions);

        for (int i = 0; i < repetitions; i++) {
            assertTrue(sessionService.deregisterPlayer(Integer.toString(i)));
        }
        assertEquals(sessionService.getSessionMap().size(), 0);
    }

    @Test
    void getPlayers() {
        sessionService = prepareSession();
        assertEquals(sessionService.getPlayers(), sessionService.getSessionMap());
    }


    @Test
    void getPlayerSessionId() {
        int repetitions = 100;
        sessionService = prepareSession();

        for (int i = 0; i < repetitions; i++) {
            assertEquals(sessionService.getPlayerSessionId(Integer.toString(i)), "value" + i);
        }
    }

    @Test
    void getNumberOfPlayers() {
        sessionService = prepareSession();

        assertEquals(sessionService.getNumberOfPlayers(), sessionService.getSessionMap().size());
    }


    SessionService prepareSession() {
        int repetitions = 100;
        sessionService.getSessionMap().clear();
        for (int i = 0; i < repetitions; i++) {
            sessionService.getSessionMap().put(Integer.toString(i), "value" + i);
        }
        return sessionService;
    }
}