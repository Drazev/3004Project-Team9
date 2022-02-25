package com.team9.questgame.gamemanager.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Data
@Service
public class GameService {
    private final int MIN_PLAYER = 2;
    private boolean gameStarted;

    @Autowired
    private SessionService sessionManager;

    public GameService() {
        this.gameStarted = false;
    }

    /**
     * Start the game
     * @return true if the game is started (or already started before), false otherwise
     */
    public synchronized boolean startGame() {
        if (!isGameStarted()) {
            // Attempt to start the game
            if (sessionManager.getNumberOfPlayers() >= MIN_PLAYER) {
                setGameStarted(true);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

}
