package com.team9.questgame.Entities.cards;

import com.team9.questgame.Entities.Players;
import com.team9.questgame.GamePhases.GamePhaseControllers;
import com.team9.questgame.exception.CardAreaException;
import com.team9.questgame.exception.IllegalGamePhaseStateException;

import java.util.*;

import static com.team9.questgame.exception.IllegalGamePhaseStateException.GamePhaseExceptionReasonCodes.NULL_ACTIVE_PHASE;

public class PlayerPlayAreas implements PlayAreas<AdventureCards> {

    private final Players player;
    private GamePhaseControllers phaseController;
    private PlayAreas<AdventureCards> targetPlayArea;
    int bids;
    int battlePoints;
    private HashMap<CardTypes, HashSet<AdventureCards>> cardTypeMap;
    private HashMap<AllCardCodes,AdventureCards> allCards;

    //Managed by Registration methods and triggered by cards
    private HashMap<AllCardCodes,HashSet<BoostableCard>> cardBoostDependencies;
    private HashSet<AdventureCards> cardsWithActiveEffects;
    private HashSet<BattlePointContributor> cardsWithBattleValue;
    private HashSet<BidContributor> cardsWithBidValue;


    public PlayerPlayAreas(Players player) {
        this.player = player;
        cardTypeMap = new HashMap<>();
        allCards = new HashMap<>();
        cardBoostDependencies = new HashMap<>();
        cardsWithActiveEffects = new HashSet<>();
        phaseController = null;
        cardsWithBattleValue = new HashSet<>();
        cardsWithBidValue = new HashSet<>();
        targetPlayArea=null;
        bids=0;
        battlePoints=0;
    }

    @Override
    public int getBattlePoints() {
        return battlePoints;
    }

    @Override
    public int getBids() {
        return bids;
    }

    void discardAllCards() {
        for(AdventureCards card : allCards.values()) {
            card.discardCard();
        }
        cardTypeMap.clear();
        allCards.clear();
        update();
    }

    public void discardAllFoes() {
        HashSet<AdventureCards> cardList = cardTypeMap.get(CardTypes.FOE);
        discardCards(cardList);
    }

    public void discardAllAllies() {
        HashSet<AdventureCards> cardList = cardTypeMap.get(CardTypes.ALLY);
        discardCards(cardList);
    }

    public void discardAllWeapons() {
        HashSet<AdventureCards> cardList = cardTypeMap.get(CardTypes.WEAPON);
        discardCards(cardList);
    }

    public void discardAllTests() {
        HashSet<AdventureCards> cardList = cardTypeMap.get(CardTypes.TEST);
        discardCards(cardList);
    }

    public void discardAllAmour() {
        HashSet<AdventureCards> cardList = cardTypeMap.get(CardTypes.AMOUR);
        discardCards(cardList);
    }

    /**
     * Add a card to the Players In play area.
     * Cards are recieved by Players Hand when they
     * attempt to play a card from their hand.
     * @param card
     */
    @Override
    public boolean receiveCard(AdventureCards card) {
        if(card==null) {
            throw new CardAreaException(CardAreaException.CardAreaExceptionReasonCodes.NULL_CARD);
        }

        AllCardCodes<AdventureDeckCards> cardCode = card.getCardCode();


        if (allCards.containsValue(card)) {
            throw new CardAreaException(CardAreaException.CardAreaExceptionReasonCodes.DUPLICATE_CARD_INSTANCE);
        }

        //GameRule:: Cannot play two of the same card
        if(allCards.containsKey(cardCode)) {
            throw new CardAreaException(CardAreaException.CardAreaExceptionReasonCodes.RULE_CANNOT_HAVE_TWO_OF_SAME_CARD_IN_PLAY);
        }

        allCards.put(card.getCardCode(),card);

        CardTypes cardType = card.getCardCode().getSubType();
        if(!cardTypeMap.containsKey(cardType))
        {
            cardTypeMap.put(cardType,new HashSet<>());
        }

        cardTypeMap.get(cardType).add(card);

        //Check to see if the new card is listed as a boost dependency list and boost all associated cards if found
        if(cardBoostDependencies.containsKey(cardCode)) {
            for(BoostableCard boostCard : cardBoostDependencies.get(cardCode)) {
                boostCard.setBoosted(true);
                card.registerBoostedCard(boostCard);
            }
        }
        update();
        return true; //Player Play area's does not reject cards
    }

    @Override
    public void discardCard(AdventureCards card) {
        card.discardCard(); //This will trigger all affected cards boost status to reset
        removeCard(card);
        update();
    }

    @Override
    public boolean playCard(AdventureCards card) {
        if(targetPlayArea==null || !allCards.containsKey(card.getCardCode())) {
            return false;
        }
        boolean rc = card.playCard(targetPlayArea);

        if(rc) {
            rc=removeCard(card);
            update();
        }
        return rc;
    }


    @Override
    public void onGameReset() {
        cardTypeMap.clear();
        allCards.clear();
        cardBoostDependencies.clear();
        cardsWithActiveEffects.clear();
        phaseController = null;
        cardsWithBattleValue.clear();
        cardsWithBidValue.clear();
        bids=0;
        battlePoints=0;
    }

    /**
     * Cards may register a boost trigger. This trigger is checked with a card is added or removed from
     * the play area.
     * @param triggerCardCode A card code that will trigger a boost for the card
     * @param card The card that would be boosted if trigger is found
     * @return
     */
    @Override
    public void registerCardBoostDependency(AllCardCodes triggerCardCode, BoostableCard card) {
        HashSet<BoostableCard> list = cardBoostDependencies.get(triggerCardCode);

        if(list==null)
        {
            list = new HashSet<>();
            cardBoostDependencies.put(triggerCardCode,list);
        }

        //If card is already in play area, trigger the boost.
        list.add(card);
        AdventureCards triggerCard = allCards.get(triggerCardCode);
        if(triggerCard!=null)
        {
            triggerCard.registerBoostedCard(card);
            card.setBoosted(true);
            update();
        }
    }

    public void registerBidContributor(BidContributor card) {
        cardsWithBidValue.add(card);
        updateBids();
    }

    public void registerBattlePointContributor(BattlePointContributor card) {
        cardsWithBattleValue.add(card);
        updateBattlePoints();
    }

    @Override
    public void removeCardBoostDependency(AllCardCodes triggerCardCode, BoostableCard card) {
        if(cardBoostDependencies.containsKey(triggerCardCode)) {
            HashSet<BoostableCard> list = cardBoostDependencies.get(triggerCardCode);
            if(list!=null) {
                list.remove(card);
            }

            if(list==null || list.size()<1) {
                cardBoostDependencies.remove(triggerCardCode);
            }
        }
    }

    @Override
    public boolean registerActiveEffect(AdventureCards card) {
        return cardsWithActiveEffects.add(card);
    }

    public void removeBidContributor(BidContributor card) {
        cardsWithBidValue.remove(card);
        updateBids();
    }

    public void removeBattlePointContributor(BattlePointContributor card) {
        cardsWithBattleValue.remove(card);
        updateBattlePoints();
    }

    @Override
    public void removeActiveEffect(AdventureCards card) {
        cardsWithActiveEffects.remove(card);
    }

    public boolean activateCard(long cardId) {
        return true; //TODO: Implement with active effects
    }

    @Override
    public void registerGamePhase(GamePhaseControllers activePhase) {
        if(activePhase==null)
        {
            throw new IllegalGamePhaseStateException(null,NULL_ACTIVE_PHASE);
        }
        this.phaseController=activePhase;
    }

    @Override
    public void onGamePhaseEnded() {
        phaseController=null;
        discardAllAmour();
        discardAllFoes();
        discardAllWeapons();
        update();
    }

    public void onPlayAreaChanged(PlayAreas targetPlayArea) {
        if(phaseController==null) {
            return;
        }
        this.targetPlayArea=targetPlayArea;
    }

    private boolean removeCard(AdventureCards card) {
        if(card==null) {
            return false;
        }

        AdventureCards delCard = allCards.get(card.cardCode);

        if(delCard!=card) {
            return false;
        }

        AllCardCodes cardCode = card.getCardCode();
        allCards.remove(cardCode);
        cardTypeMap.get(cardCode.getSubType()).remove(card);
        return true;
    }
    private void update() {
        updateBids();
        updateBattlePoints();
    }

    private void updateBids() {
        int newBids=0;

        for(BidContributor card : cardsWithBidValue) {
            newBids+=card.getBids();
        }

        bids=newBids;
    }

    private void updateBattlePoints() {
        int newBattlePoints=0;

        for(BattlePointContributor card : cardsWithBattleValue) {
            newBattlePoints+=card.getBattlePoints();
        }
        battlePoints=newBattlePoints;
    }

    /**
     * Discards all cards from play area in list.
     *
     * @sideEffects Card discard function will trigger all observing boosted cards to clear boost status.
     * @param cardList The list of cards to be discarded
     */
    private void discardCards(HashSet<AdventureCards> cardList)
    {
        for(AdventureCards card : cardList) {
            discardCard(card);
        }
        cardList.clear();
        update();
    }
}
