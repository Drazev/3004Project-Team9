package com.team9.questgame.gamemanager.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SessionServiceTest {

    @Test
    void registerPlayer() {
        int repetitions = 100;
        SessionService sessionService = new SessionService();

        for (int i=0; i<repetitions; i++) {
            assertTrue(sessionService.registerPlayer(Integer.toString(i)));
        }
        assertEquals(sessionService.getSessionMap().size(), repetitions);

        for (int i=0; i<repetitions; i++) {
            assertFalse(sessionService.registerPlayer(Integer.toString(i)));
        }
        assertEquals(sessionService.getSessionMap().size(), repetitions);

    }

    @Test
    void deregisterPlayer() {
        int repetitions = 100;
        SessionService sessionService = prepareSession();

        for (int i=0; i<repetitions; i++) {
            assertFalse(sessionService.deregisterPlayer("lol" + i));
        }
        assertEquals(sessionService.getSessionMap().size(), repetitions);

        for (int i=0; i<repetitions; i++) {
            assertTrue(sessionService.deregisterPlayer(Integer.toString(i)));
        }
        assertEquals(sessionService.getSessionMap().size(), 0);
    }

    @Test
    void getPlayers() {
        SessionService sessionService = prepareSession();
        assertEquals(sessionService.getPlayers(), sessionService.getSessionMap());
    }


    @Test
    void getPlayerSessionId() {
        int repetitions = 100;
        SessionService sessionService = prepareSession();

        for (int i=0; i<repetitions; i++) {
            assertEquals(sessionService.getPlayerSessionId(Integer.toString(i)), "value" + i);
        }
    }

    @Test
    void getNumberOfPlayers() {
        SessionService sessionService = prepareSession();

        assertEquals(sessionService.getNumberOfPlayers(), sessionService.getSessionMap().size());
    }


    SessionService prepareSession() {
        int repetitions = 100;
        SessionService sessionService = new SessionService();
        for (int i=0; i<repetitions; i++) {
            sessionService.getSessionMap().put(Integer.toString(i), "value" + i);
        }
        return sessionService;
    }
}