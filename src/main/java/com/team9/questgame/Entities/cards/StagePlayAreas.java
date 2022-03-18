package com.team9.questgame.Entities.cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Data.CardData;
import com.team9.questgame.Data.PlayAreaData;
import com.team9.questgame.Data.PlayAreaDataSources;
import com.team9.questgame.Data.StageAreaData;
import com.team9.questgame.exception.CardAreaException;
import com.team9.questgame.game_phases.quest.QuestPhaseController;
import com.team9.questgame.gamemanager.service.OutboundService;
import com.team9.questgame.gamemanager.service.QuestPhaseOutboundService;
import lombok.Getter;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class StagePlayAreas implements PlayAreas<AdventureCards>{

    @JsonIgnore
    private final QuestPhaseOutboundService outboundService;
    private int stageNum;
    private long id;
    private int battlePoints;
    private int bids;
    @JsonIgnore
    private Logger LOG;
    @Getter
    private HashMap<AllCardCodes, AdventureCards> allCards;
    private QuestCards questCard;
    private QuestPhaseController phaseController;
    private HashMap<AllCardCodes,HashSet<BoostableCard>> cardBoostDependencies;
    private HashSet<BattlePointContributor> cardsWithBattleValue;
    private HashSet<BoostableCard> boostableCards;


    private StagePlayAreas targetPlayArea;




    static private int nextid = 0;

    public StagePlayAreas(QuestCards questCard, int stageNum){
        this.id = nextid++;
        this.questCard = questCard;
        this.stageNum = stageNum;
        this.allCards = new HashMap<>();
        this.battlePoints = 0;
        this.bids = 0;
        this.outboundService = ApplicationContextHolder.getContext().getBean(QuestPhaseOutboundService.class);
        cardBoostDependencies = new HashMap<>();
        boostableCards = new HashSet<>();

        cardsWithBattleValue = new HashSet<>();

        this.targetPlayArea=null;

    }

    @Override
    public boolean receiveCard(AdventureCards card){
        if(card==null) {
            throw new CardAreaException(CardAreaException.CardAreaExceptionReasonCodes.NULL_CARD);
        }
        return addToPlayArea(card);
    }

    public void receiveCard(QuestCards questCard){
        this.questCard = questCard;
    }



    private boolean addToPlayArea(AdventureCards card){
        if(allCards.containsKey(card.getCardCode())){
            LOG.error("RULE: A stage cannot have two cards of the same type");
            throw new CardAreaException(CardAreaException.CardAreaExceptionReasonCodes.RULE_CANNOT_HAVE_TWO_OF_SAME_CARD_IN_PLAY);
        }
        allCards.put(card.getCardCode(),  card);
        if(card.getSubType() == CardTypes.FOE){
            if(questCard.getBoostedFoe().equals(card.getCardCode())){

                for(BoostableCard boostCard : cardBoostDependencies.get(card.getCardCode())){
                    boostCard.setBoosted(true);
                    card.registerBoostedCard(boostCard);
                }
            }
        }


        return true;
    }

    @Override
    public boolean playCard(AdventureCards card){
        if(targetPlayArea==null) {
            return false;
        }
        boolean rc = card.playCard(targetPlayArea);

        if(rc) {
            rc=removeCard(card);
            updateBattlePoints();
        }
        return rc;
    }
    /**
     * Helper method to remove a card from the PlayerPlayArea and all its tracking data structures.
     *
     * Cards do not unregister themselves, they are removed by this function when they leave the play
     * area for any reason.
     * @param card The card to be removed
     * @return True if the card was found and removed, False otherwise
     */
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
        cardBoostDependencies.remove(cardCode);
        cardsWithBattleValue.remove(delCard);
        boostableCards.remove(delCard);
        return true;
    }

    public void onPlayAreaChanges(StagePlayAreas targetPlayArea){
        if(phaseController == null){
            throw new CardAreaException(CardAreaException.CardAreaExceptionReasonCodes.GAMEPHASE_NOT_REGISTERED);
        }
        this.targetPlayArea=targetPlayArea;
    }


    public boolean discardAllCards() {
        HashSet<AdventureCards> cardList = new HashSet<>(allCards.values());
        return discardCards(cardList);

    }


    @Override
    public void discardCard(AdventureCards card){
        HashSet<AdventureCards> cardList = new HashSet<>();
        cardList.add(card);
        discardCards(cardList);
    }

    /**
     * Discards all cards from play area in list.
     *
     * @sideEffects Card discard function will trigger all observing boosted cards to clear boost status.
     * @param cardList The list of cards to be discarded
     * @return True if at least one card was discarded
     */
    private boolean discardCards(HashSet<AdventureCards> cardList)
    {
        HashSet<AdventureCards> list = new HashSet<>(cardList);
        boolean rc = !list.isEmpty();
        for(AdventureCards card : list) {
            card.discardCard();
            removeCard(card);
        }
        cardList.clear();
        updateBattlePoints();
        return rc;
    }

    @Override
    public void onGameReset(){
        allCards.clear();
        cardBoostDependencies.clear();
        phaseController = null;
        cardsWithBattleValue.clear();
        boostableCards.clear();
        bids=0;
        battlePoints=0;
        questCard=null;
        targetPlayArea=null;
    }

    @Override
    public int getBattlePoints(){return battlePoints;}

    @Override
    public int getBids(){return bids;}

    @Override
    public void onGamePhaseEnded(){

    }

    public int size() {
        return allCards.size();
    }

    public void registerBattlePointContributor(BattlePointContributor card){
        cardsWithBattleValue.add(card);
        updateBattlePoints();
        notifyStageAreaChanged();
    }

    @Override
    public void registerBoostableCard(BoostableCard card) {
        boostableCards.add(card);
        if(questCard.getBoostedFoe() == card.getCardCode()){
            card.setBoosted(true);
        }
    }


    /**
     * Recalculates the battlepoint value based on cards in the play area and the player rank.
     */
    private void updateBattlePoints() {
        int newBattlePoints=0;

        for(BattlePointContributor card : cardsWithBattleValue) {
            newBattlePoints+=card.getBattlePoints();
        }
        battlePoints=newBattlePoints;
        notifyStageAreaChanged();
    }

    public StageAreaData getStageAreaData() {
        HashSet<CardTypes> allowedTypes = new HashSet<>();
        allowedTypes.add(CardTypes.FOE);
        allowedTypes.add(CardTypes.WEAPON);
        allowedTypes.add(CardTypes.AMOUR);
        StageAreaData data = new StageAreaData(
                id,
                stageNum,
                bids,
                battlePoints,
                allowedTypes,
                getCardData()
        );
        return data;
    }
    public ArrayList<CardData> getCardData() {
        ArrayList<CardData> handCards = new ArrayList<>();
        for(Cards card : allCards.values()) {
            handCards.add(card.generateCardData());
        }
        return handCards;
    }

    /**
     * Updates the clients about a stage area being changed, sending the new state.
     */
    private void notifyStageAreaChanged() {
        outboundService.broadcastStageChanged(getStageAreaData());
    }
}

