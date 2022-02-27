package com.team9.questgame.Entities.cards;

/**
 * Identifies a valid area that holds cards and
 * functions required to work with the Decks
 */
public interface CardArea {
    void receiveCard(Cards card);
    void discardCard(long cardId);
    void playCard(long cardId);
    void onGameReset();
}
