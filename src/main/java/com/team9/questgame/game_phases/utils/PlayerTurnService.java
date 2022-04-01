package com.team9.questgame.game_phases.utils;

import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.gamemanager.record.socket.NotificationOutbound;
import com.team9.questgame.gamemanager.service.NotificationOutboundService;
import com.team9.questgame.gamemanager.service.OutboundService;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Keep track of player's turn, each game phase should have one of this
 */
public class PlayerTurnService {
    private int currentPlayerIndex;
    @Getter
    private final ArrayList<Players> players;

    public PlayerTurnService(ArrayList<Players> players) {
        this.players = players;
        this.currentPlayerIndex = 0;
    }

    public Players getPlayerTurn() {
        if (players.size() == 0) {
            throw new RuntimeException("No player found");
        }
        return players.get(currentPlayerIndex);
    }

    public int getPlayerIndexTurn() {
        if (players.size() == 0) {
            throw new RuntimeException("No player found");
        }
        return currentPlayerIndex;
    }

    public void nextPlayer() {
        if (players.size() == 0) {
            throw new RuntimeException("No player found");
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        notifyTurnChange();
    }

    public void nextPlayerExcept(Players p) {
        // TODO: Make test case for this
        if (players.size() == 0) {
            throw new RuntimeException("No player found");
        }
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (getPlayerTurn().getPlayerId() == p.getPlayerId());

        notifyTurnChange();
    }

    public boolean setPlayerTurn(int i) {
        if (i >= 0 && i < players.size()) {
            currentPlayerIndex = i;
            return true;
        }
        return false;
    }

    public boolean setPlayerTurn(Players player) {
        int i = players.indexOf(player);
        if (i != -1) {
            currentPlayerIndex = i;
            this.notifyTurnChange();
            return true;
        }
        return false;
    }

    public void onGameReset() {
        currentPlayerIndex = 0;
    }

    public void notifyTurnChange() {
        // TODO: Add test for this broadcast in the OutboundServiceTest or WSControllerTest
        NotificationOutbound msgToPlayer = new NotificationOutbound("Player Turn","It is your turn!","","");
        NotificationOutboundService.getService().sendGoodNotification(getPlayerTurn(),msgToPlayer,null);
        OutboundService.getService().broadcastNextTurn(getPlayerTurn());
    }

}
