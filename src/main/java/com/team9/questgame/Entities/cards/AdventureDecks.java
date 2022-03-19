package com.team9.questgame.Entities.cards;

import com.team9.questgame.Entities.DeckTypes;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

public class AdventureDecks extends Decks<AdventureCards,AdventureDeckCards>{

    public AdventureDecks() {
         super(DeckTypes.ADVENTURE, AdventureDecks.class);
    }

    public AdventureCards drawCard(Hand hand) {
        AdventureCards card=selectCard(hand);
        card.playCard(hand);
        return card;
    }

    @Override
    public void createDeck() {
        HashMap<AdventureDeckCards,Integer> deckList = new HashMap<>();

        //Weapons
        deckList.put(AdventureDeckCards.EXCALIBUR,2);
        deckList.put(AdventureDeckCards.LANCE,6);
        deckList.put(AdventureDeckCards.BATTLE_AX,8);
        deckList.put(AdventureDeckCards.SWORD,16);
        deckList.put(AdventureDeckCards.HORSE,11);
        deckList.put(AdventureDeckCards.DAGGER,6);

        //Foes
        deckList.put(AdventureDeckCards.DRAGON,1);
        deckList.put(AdventureDeckCards.GIANT,2);
        deckList.put(AdventureDeckCards.MORDRED,4);
        deckList.put(AdventureDeckCards.GREEN_KNIGHT,2);
        deckList.put(AdventureDeckCards.BLACK_KNIGHT,3);
        deckList.put(AdventureDeckCards.EVIL_KNIGHT,6);
        deckList.put(AdventureDeckCards.SAXON_KNIGHT,8);
        deckList.put(AdventureDeckCards.ROBBER_KNIGHT,7);
        deckList.put(AdventureDeckCards.SAXONS,5);
        deckList.put(AdventureDeckCards.BOAR,4);
        deckList.put(AdventureDeckCards.THIEVES,8);

        //Tests
        deckList.put(AdventureDeckCards.TEST_OF_VALOR,2);
        deckList.put(AdventureDeckCards.TEST_OF_TEMPTATION,2);
        deckList.put(AdventureDeckCards.TEST_OF_MORGAN_LE_FEY,2);
        deckList.put(AdventureDeckCards.TEST_OF_THE_QUESTING_BEAST,2);

        //Allies

        deckList.put(AdventureDeckCards.SIR_GALAHAD,1);
        deckList.put(AdventureDeckCards.SIR_LANCELOT,1);
        deckList.put(AdventureDeckCards.KING_ARTHUR,1);
        deckList.put(AdventureDeckCards.SIR_TRISTAN,1);
        deckList.put(AdventureDeckCards.KING_PELLINORE,1);
        deckList.put(AdventureDeckCards.SIR_GAWAIN,1);
        deckList.put(AdventureDeckCards.SIR_PERCIVAL,1);
        deckList.put(AdventureDeckCards.QUEEN_GUINEVERE,1);
        deckList.put(AdventureDeckCards.QUEEN_ISEULT,1);
        deckList.put(AdventureDeckCards.MERLIN,1);

        //Amours
        deckList.put(AdventureDeckCards.AMOUR,8);

        createDeck(deckList);
    }

    @Override
    public void testRebuildDeckWithList(HashMap<AdventureDeckCards, Integer> deckList) {
        super.testRebuildDeckWithList(deckList);
        createDeck(deckList);
    }

    public void createDeck(HashMap<AdventureDeckCards,Integer> deckList) {

        //Iterate through each card in list
        for(Map.Entry<AdventureDeckCards,Integer> e : deckList.entrySet()) {

            //Create number of cards as proscribed in list
            for(int i=0;i<e.getValue();++i)
            {
                AdventureCards card = factory.createCard(this,e.getKey());
                if(card==null) {
                    //TODO: Log error
                }
                else {
                    cardsInDeck.add(card);
                }
            }
        }
    }
}
