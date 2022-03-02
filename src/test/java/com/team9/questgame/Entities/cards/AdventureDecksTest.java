package com.team9.questgame.Entities.cards;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class AdventureDecksTest <T extends Cards> {

    @Autowired
    private AdventureDecks deck;


    @Test
    void createDeck() {

        assert(deck.cardsInDeck.size()==125);


        for(Cards card : deck.cardsInDeck) {
            System.out.println(card);
        }




    }
}