package com.team9.questgame.Entities.cards;

import java.util.HashMap;
import java.util.Map;

public class StoryDecks extends Decks {
    public StoryDecks() {
        super();
    }

    @Override
    protected void createDeck() {
        HashMap<StoryDeckCards,Integer> deckList = new HashMap<>();

        //Quests
        deckList.put(StoryDeckCards.SEARCH_FOR_THE_HOLY_GRAIL,1);
        deckList.put(StoryDeckCards.TEST_OF_THE_GREEN_KNIGHT,1);
        deckList.put(StoryDeckCards.SEARCH_FOR_THE_QUESTING_BEAST,1);
        deckList.put(StoryDeckCards.DEFEND_THE_QUEENS_HONOR,1);
        deckList.put(StoryDeckCards.RESCUE_THE_FAIR_MAIDEN,1);
        deckList.put(StoryDeckCards.JOURNEY_THROUGH_THE_ENCHANTED_FOREST,1);
        deckList.put(StoryDeckCards.VANQUISH_KING_ARTHURS_ENEMIES,2);
        deckList.put(StoryDeckCards.SLAY_THE_DRAGON,1);
        deckList.put(StoryDeckCards.BOAR_HUNT,2);
        deckList.put(StoryDeckCards.REPEL_THE_SAXON_RAIDERS,2);

        //Tournaments
        deckList.put(StoryDeckCards.TOURNAMENT_AT_CAMELOT,1);
        deckList.put(StoryDeckCards.TOURNAMENT_AT_ORKNEY,1);
        deckList.put(StoryDeckCards.TOURNAMENT_AT_TINTAGEL,1);
        deckList.put(StoryDeckCards.TOURNAMENT_AT_YORK,1);

        //Events
        deckList.put(StoryDeckCards.KINGS_RECOGNITION,2);
        deckList.put(StoryDeckCards.QUEENS_FAVOR,2);
        deckList.put(StoryDeckCards.COURT_CALLED_TO_CAMELOT,2);
        deckList.put(StoryDeckCards.POX,1);
        deckList.put(StoryDeckCards.PLAGUE,1);
        deckList.put(StoryDeckCards.CHIVALROUS_DEED,1);
        deckList.put(StoryDeckCards.PROSPERITY_THROUGHOUT_THE_REALM,1);
        deckList.put(StoryDeckCards.KINGS_CALL_TO_ARMS,1);

        //Iterate through each card in list
        for(Map.Entry<StoryDeckCards,Integer> e : deckList.entrySet()) {

            //Create number of cards as proscribed in list
            for(int i=0;i<e.getValue();++i)
            {
                StoryCards card = factory.createCard(this,e.getKey());
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

