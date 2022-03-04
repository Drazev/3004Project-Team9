package com.team9.questgame.Entities.cards;

import com.team9.questgame.Entities.Players;
import com.team9.questgame.QuestGameController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;


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