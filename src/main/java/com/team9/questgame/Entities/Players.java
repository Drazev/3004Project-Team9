package com.team9.questgame.Entities;

import com.team9.questgame.Entities.cards.Cards;

/**
 * Entity representation of a Player registered to an individual game session.
 * This class governs the individual actions a player can choose or be forced to take.
 * Actions that are player driven are prefixed with 'action' while events driven by the game
 * system are prefixed with 'on'
 *
 * This class emits events when a player discards a card or is has an oversize hand.
 *
 * This class owns a PlayerArea and contains a hand;
 */
public class Players {
    private final long playerId; //Unique to player game session. Does not persist between games.
    private String name;
    private int battlePoints;
    private int shields;
    private boolean isHandOversize;
    private static long nextId=0;

    public Players(String playerName)
    {
        this.playerId=nextId++;
        this.battlePoints=0;
        this.shields=0;
        this.isHandOversize=false;
    }

    public long getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBattlePoints() {
        return battlePoints;
    }

    public int getShields() {
        return shields;
    }

    public boolean isHandOversize() {
        return isHandOversize;
    }

    public void onCardIssued(Cards newCard) {

    }

    public void onGameRest() {

    }

    public boolean onEndOfPhase() {
        boolean rc=false;

        return rc;
    }

    public boolean actionPlayCard(long cardId) {
        boolean rc=false;

        return rc;
    }

    public boolean actionDiscardCard(long cardId ) {
        boolean rc=false;

        return rc;
    }

    public boolean actionEndTurn() {
        boolean rc=false;

        return rc;
    }

    private boolean validateHandSize() {
        boolean rc=false;

        return rc;
    }

    private void eventHandOversize() {

    }

}
