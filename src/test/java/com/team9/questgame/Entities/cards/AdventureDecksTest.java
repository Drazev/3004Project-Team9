package com.team9.questgame.Entities.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class AdventureDecksTest <T extends Cards> {

    private AdventureDecks deck;

    @BeforeEach
    void setUp() {
        deck = new AdventureDecks();
    }

    @Test
    void createDeck() {

        assert(deck.cardsInDeck.size()==125);


        for(Cards card : deck.cardsInDeck) {
            System.out.println(card);
        }





    }
}