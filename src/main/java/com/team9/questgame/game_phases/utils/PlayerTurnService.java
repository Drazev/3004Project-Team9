package com.team9.questgame.game_phases.utils;

import com.team9.questgame.Entities.Players;

import java.util.ArrayList;

/**
 * Keep track of player's turn, each game phase should have one of this
 */
public class PlayerTurnService {
    private int currentPlayerIndex;
    private final ArrayList<Players> players;

    public PlayerTurnService(ArrayList<Players> players) {
        this.players = players;
        currentPlayerIndex = 0;
    }

    public Players nextPlayerTurn() {
        if (players.size() == 0) {
            throw new RuntimeException("No player found");
        }

        Players returnValue = players.get(currentPlayerIndex);
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        return returnValue;
    }

    public int nextPlayerIndexTurn() {
        if (players.size() == 0) {
            throw new RuntimeException("No player found");
        }
        int returnValue = currentPlayerIndex;
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        return returnValue;
    }


}
