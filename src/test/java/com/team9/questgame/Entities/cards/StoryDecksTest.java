package com.team9.questgame.Entities.cards;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StoryDecksTest {

    @Test
    void createDeck() {
        StoryDecks deck = new StoryDecks();
        assert(deck.cardsInDeck.size()==28);
    }
}