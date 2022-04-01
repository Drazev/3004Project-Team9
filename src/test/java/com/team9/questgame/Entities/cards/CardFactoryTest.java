package com.team9.questgame.Entities.cards;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CardFactoryTest {

    @Test
    void getInstance() {
        CardFactory f = CardFactory.getInstance();
        assert(f!=null);
        CardFactory g = CardFactory.getInstance();
        assert(g!=null);
        assert(g==f);
    }

    @Test
    void CreateCard() {
        AdventureDecks adventureDeck = new AdventureDecks();
        StoryDecks storyDeck = new StoryDecks();
        CardFactory f = CardFactory.getInstance();
        ArrayList<Cards> deck = new ArrayList<>();
        ArrayList<Cards> deck2 = new ArrayList<>();
        ArrayList<Cards> sDeck = new ArrayList<>();
        ArrayList<Cards> sDeck2 = new ArrayList<>();
        for(AdventureDeckCards code : AdventureDeckCards.values())
        {
            Cards card = f.createCard(adventureDeck,code);
            assert(card!=null);
            deck.add(card);
        }

        for(AdventureDeckCards code : AdventureDeckCards.values())
        {
            Cards card = f.createCard(adventureDeck,code);
            assert(card!=null);
            deck2.add(card);
        }

        for(StoryDeckCards code : StoryDeckCards.values())
        {
            Cards card = f.createCard(storyDeck,code);
            assert(card!=null);
            sDeck.add(card);
        }

        for(StoryDeckCards code : StoryDeckCards.values())
        {
            Cards card = f.createCard(storyDeck,code);
            assert(card!=null);
            sDeck2.add(card);
        }



        for(int i=0;i<AdventureDeckCards.values().length;++i)
        {
            Cards c1 = deck.get(i);
            Cards c2 = deck2.get(i);
            assert(c1.cardID!=c2.cardID);
            assert(c1.cardCode==c2.cardCode);
            assert(c1.subType==c2.subType);
            assert(c1.cardName.equalsIgnoreCase(c2.cardName));
            assert(c1.imgSrc.equalsIgnoreCase(c2.imgSrc));
            assert( (c1.activeAbilityDescription==null && c2.activeAbilityDescription==null) || (c1.activeAbilityDescription.equalsIgnoreCase(c2.activeAbilityDescription)));
            assert(!c1.equals(c2));
            assert(c1.equals(c1));
        }

        for(int i=0;i<StoryDeckCards.values().length;++i)
        {
            Cards c1 = sDeck.get(i);
            Cards c2 = sDeck2.get(i);
            assert(c1.cardID!=c2.cardID);
            assert(c1.cardCode==c2.cardCode);
            assert(c1.subType==c2.subType);
            assert(c1.cardName.equalsIgnoreCase(c2.cardName));
            assert(c1.imgSrc.equalsIgnoreCase(c2.imgSrc));
            assert( (c1.activeAbilityDescription==null && c2.activeAbilityDescription==null) || (c1.activeAbilityDescription.equalsIgnoreCase(c2.activeAbilityDescription)));
            assert(!c1.equals(c2));
            assert(c1.equals(c1));
        }
    }

    @Test
    void testCreateCard() {
        AdventureDecks adventureDeck = new AdventureDecks();
        CardFactory f = CardFactory.getInstance();
        ArrayList<Cards> deck = new ArrayList<>();
        ArrayList<Cards> deck2 = new ArrayList<>();
        for(AdventureDeckCards code : AdventureDeckCards.values())
        {
            Cards card = f.createCard(adventureDeck,code);
            assert(card!=null);
            deck.add(card);
        }

        for(AdventureDeckCards code : AdventureDeckCards.values())
        {
            Cards card = f.createCard(adventureDeck,code);
            assert(card!=null);
            deck2.add(card);
        }

        for(int i=0;i<AdventureDeckCards.values().length;++i)
        {
            Cards c1 = deck.get(i);
            Cards c2 = deck2.get(i);
            assert(c1.cardID!=c2.cardID);
            assert(c1.cardCode==c2.cardCode);
            assert(c1.subType==c2.subType);
            assert(c1.cardName.equalsIgnoreCase(c2.cardName));
            assert(c1.imgSrc.equalsIgnoreCase(c2.imgSrc));
            assert( (c1.activeAbilityDescription==null && c2.activeAbilityDescription==null) || (c1.activeAbilityDescription.equalsIgnoreCase(c2.activeAbilityDescription)));
            assert(!c1.equals(c2));
            assert(c1.equals(c1));
        }
    }

    @Test
    void testCreateCard1() {
        StoryDecks storyDeck = new StoryDecks();
        CardFactory f =  CardFactory.getInstance();
        ArrayList<Cards> deck = new ArrayList<>();
        ArrayList<Cards> deck2 = new ArrayList<>();
        for(StoryDeckCards code : StoryDeckCards.values())
        {
            Cards card = f.createCard(storyDeck,code);
            assert(card!=null);
            deck.add(card);
        }

        for(StoryDeckCards code : StoryDeckCards.values())
        {
            Cards card = f.createCard(storyDeck,code);
            assert(card!=null);
            deck2.add(card);
        }

        for(int i=0;i<StoryDeckCards.values().length;++i)
        {
            Cards c1 = deck.get(i);
            Cards c2 = deck2.get(i);
            assert(c1.cardID!=c2.cardID);
            assert(c1.cardCode==c2.cardCode);
            assert(c1.subType==c2.subType);
            assert(c1.cardName.equalsIgnoreCase(c2.cardName));
            assert(c1.imgSrc.equalsIgnoreCase(c2.imgSrc));
            assert( (c1.activeAbilityDescription==null && c2.activeAbilityDescription==null) || (c1.activeAbilityDescription.equalsIgnoreCase(c2.activeAbilityDescription)));
            assert(!c1.equals(c2));
            assert(c1.equals(c1));
        }


    }
}