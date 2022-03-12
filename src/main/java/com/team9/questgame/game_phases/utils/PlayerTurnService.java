package com.team9.questgame.game_phases.utils;

import com.team9.questgame.Entities.Players;
import lombok.Getter;

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
        currentPlayerIndex = 0;
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
            return true;
        }
        return false;
    }

    public void onGameReset() {
        currentPlayerIndex = 0;
    }

}
