package com.team9.questgame.gamemanager.service;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.game_phases.GeneralGameController;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class InboundService {
    private boolean gameStarted;
    private Logger LOG;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private OutboundService outboundService;

    @Autowired
    private GeneralGameController gameController;

    public InboundService() {
        this.LOG = LoggerFactory.getLogger(InboundService.class);
        this.gameStarted = false;
    }

    /**
     * Start the game
     *
     * @return true if the game is started (or already started before), false otherwise
     */
    public synchronized boolean startGame() {
        final int MIN_PLAYER = 2;
        if (!isGameStarted()) {
            // Attempt to start the game
            if (sessionService.getNumberOfPlayers() >= MIN_PLAYER) {
                boolean isGameAlreadyStarted = isGameStarted();
                if (!isGameAlreadyStarted) {
                    // The first time that the game started
                    setGameStarted(true);
                    for (Players player : sessionService.getPlayerMap().values()) {
                        gameController.playerJoin(player);
                    }
                    gameController.startGame();
                }
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public synchronized void playerDrawCard(String name, long cardId) {
        // Note: cardId is unused
        Players player = sessionService.getPlayerMap().get(name);
        gameController.dealCard(player);
    }

    public synchronized void playerDiscardCard(String name, long cardId) {
        LOG.info(String.format("Player named %s requested to discard a card with id %ld", name, cardId));
        gameController.playerDiscardCard(sessionService.getPlayerMap().get(name), cardId);
    }

    public synchronized void playerNotifyHandOversize() {
        LOG.info("NotifyHandOversized sent to Game Controller.");
        // Currently, the gameController doesn't need to know whose hand is oversize
        gameController.handlePlayerHandOversize();
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }
}
