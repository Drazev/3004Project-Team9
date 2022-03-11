package com.team9.questgame.Entities.cards;

/**
 * Identifies a valid area that holds cards and
 * functions required to work with the Decks
 */
public interface CardArea<T extends Cards> {
    /**
     * Cards are delt to this
     * @param card
     */
    boolean receiveCard(T card);
    void discardCard(T card);
    boolean playCard(T card);
    void onGameReset();
}
