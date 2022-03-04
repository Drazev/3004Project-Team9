package com.team9.questgame.gamemanager.service;

import com.team9.questgame.gamemanager.model.CardEvent;
import com.team9.questgame.gamemanager.model.EmptyJsonReponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class GameService {
    private boolean gameStarted;
    private final Logger LOG = LoggerFactory.getLogger(GameService.class);

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
        final int MIN_PLAYER = 2;
        if (!isGameStarted()) {
            // Attempt to start the game
            if (sessionService.getNumberOfPlayers() >= MIN_PLAYER) {
                boolean isGameAlreadyStarted = isGameStarted();
                if (!isGameAlreadyStarted) {
                    setGameStarted(true);
                    // broadcast only the first time that the game is started
                    broadcastGameStart();
                }
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public synchronized void playerDrawCard(CardEvent cardEvent) {
        // Do some business logic

        // Broadcast (optional - other internal objects can directly call the broadcast function)
        // broadcastPlayerDrawCard(cardEvent);
    }

    public synchronized void playerDiscardCard(CardEvent cardEvent) {
        // Do some business logic

        // Broadcast (optional - other internal objects can directly call the broadcast function)
        // broadcastPlayerDiscardCard(cardEvent);
    }


    /**
     * Broadcast player-draw-connect event
     */
    public void broadcastPlayerConnect() {
        Map<String, String> players = sessionService.getPlayers();
        this.sendToAllPlayers("/topic/general/player-connect", players);
    }

    /**
     * Broadcast player-draw-connect event
     */
    public void broadcastPlayerDisconnect() {
        Map<String, String> players = sessionService.getPlayers();
        this.sendToAllPlayers("/topic/general/player-disconnect", players);
    }

    public void broadcastGameStart() {
        // @TODO: add payload to include the name of the first player's turn
        this.sendToAllPlayers("/topic/general/game-start");
    }

    public void broadcastNextTurn() {
        // @TODO: add payload to include the name of the next player's turn
        this.sendToAllPlayers("/topic/general/next-turn");
    }

    /**
     * Broadcast player-draw-card event
     * @param cardEvent the CardEvent payload
     */
    public void broadcastPlayerDrawCard(CardEvent cardEvent) {
        this.sendToAllPlayers("/topic/general/player-draw-card", cardEvent);
    }

    /**
     * Broadcast player-discard-card event
     * @param cardEvent the CardEvent payload
     */
    public void broadcastPlayerDiscardCard(CardEvent cardEvent) {
        // @TODO: add payload to include the cardId and card holder's name
        this.sendToAllPlayers("/topic/general/player-discard-card", cardEvent);
    }

    private void sendToPlayer(String topic, String name, Object payload) {
        LOG.info(String.format("Broadcasting to one players: topic=%s, name=%s, payload=%s", topic, name, payload));
        messenger.convertAndSendToUser(topic, sessionService.getPlayerSessionId(name), payload);
    }

    private void sendToAllPlayers(String topic, Object payload) {
        LOG.info(String.format("Broadcasting to one players: topic=%s, payload=%s", topic, payload));
        messenger.convertAndSend(topic, payload);
    }

    private void sendToAllPlayers(String topic) {
        LOG.info(String.format("Broadcasting to one players: topic=%s", topic));
        messenger.convertAndSend(topic, new EmptyJsonReponse());
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }
}
