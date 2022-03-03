package com.team9.questgame.Entities.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class StoryDecksTest {

    private StoryDecks deck;

    @BeforeEach
    void setUp() {
        deck = new StoryDecks();
    }

    @Test
    void createDeck() {

        assert(deck.cardsInDeck.size()==28);
        for(Cards card : deck.cardsInDeck) {
            System.out.println(card);
            assert(deck==card.assignedDeck);
        }
    }
}