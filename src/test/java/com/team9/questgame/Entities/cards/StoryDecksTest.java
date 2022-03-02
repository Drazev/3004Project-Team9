package com.team9.questgame.Entities.cards;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StoryDecksTest {
    @Autowired
    private StoryDecks deck;

    @Test
    void createDeck() {

        assert(deck.cardsInDeck.size()==28);
        for(Cards card : deck.cardsInDeck) {
            System.out.println(card);
            assert(deck==card.assignedDeck);
        }
    }
}