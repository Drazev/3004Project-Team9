package com.team9.questgame.gamemanager.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GameService {
    private final int MIN_PLAYER = 2;
    private boolean gameStarted;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SimpMessagingTemplate messenger;

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
            if (sessionService.getNumberOfPlayers() >= MIN_PLAYER) {
                setGameStarted(true);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void sendToPlayer(String topic, String name, Object payload) {
        messenger.convertAndSendToUser(topic, sessionService.getPlayerSessionId(name), payload);
    }

    public void sendToAllPlayers(String topic, Object payload) {
        messenger.convertAndSend(topic, payload);
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }
}
