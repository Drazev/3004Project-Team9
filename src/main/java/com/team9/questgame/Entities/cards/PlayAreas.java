package com.team9.questgame.Entities.cards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class PlayAreas implements CardArea<AdventureCards> {
    private HashMap<AdventureDeckCards, ArrayList<AdventureCards>> cardTypeMap;
    private HashSet<AdventureCards> allCards;
    private QuestCards activeQuest;



    void setActiveQuest(QuestCards activeQuest) {

    }

    void discardAllCards() {
        for(AdventureCards card : allCards) {
            card.discardCard();
        }
        cardTypeMap.clear();
        allCards.clear();
    }

    void discardAllFoes() {
        for(Map.Entry<AdventureDeckCards,ArrayList<AdventureCards>> entry : cardTypeMap.entrySet()) {
            if(entry.getKey().subType==CardTypes.FOE) {
                for(AdventureCards card: entry.getValue()) {
                    card.discardCard();
                }
            }
            entry.getValue().clear();
        }
    }

    void discardAllAllies() {
        for(Map.Entry<AdventureDeckCards,ArrayList<AdventureCards>> entry : cardTypeMap.entrySet()) {
            if(entry.getKey().subType==CardTypes.ALLY) {
                for(AdventureCards card: entry.getValue()) {
                    card.discardCard();
                }
            }
            entry.getValue().clear();
        }
    }

    void discardAllWeapons() {
        for(Map.Entry<AdventureDeckCards,ArrayList<AdventureCards>> entry : cardTypeMap.entrySet()) {
            if(entry.getKey().subType==CardTypes.WEAPON) {
                for(AdventureCards card: entry.getValue()) {
                    card.discardCard();
                }
            }
            entry.getValue().clear();
        }
    }

    void discardAllTests() {
        for(Map.Entry<AdventureDeckCards,ArrayList<AdventureCards>> entry : cardTypeMap.entrySet()) {
            if(entry.getKey().subType==CardTypes.TEST) {
                for(AdventureCards card: entry.getValue()) {
                    card.discardCard();
                }
            }
            entry.getValue().clear();
        }
    }

    void discardAllAmour() {
        for(Map.Entry<AdventureDeckCards,ArrayList<AdventureCards>> entry : cardTypeMap.entrySet()) {
            if(entry.getKey().subType==CardTypes.AMOUR) {
                for(AdventureCards card: entry.getValue()) {
                    card.discardCard();
                }
            }
            entry.getValue().clear();
        }
    }

    /**
     * Add a card to the Players In play area
     * @param card
     */
    @Override
    public void receiveCard(AdventureCards card) {

    }

    @Override
    public void discardCard(AdventureCards card) {

    }

    /**
     * Trigger a card's active ability if it exists
     * @param card The card to be triggered.
     */
    @Override
    public void playCard(AdventureCards card) {

    }

    @Override
    public void onGameReset() {

    }

    public void onQuestStart(StoryCards activeQuest) {

    }


}
