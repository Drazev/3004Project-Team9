package com.team9.questgame.Entities.cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Data.CardData;
import com.team9.questgame.Data.PlayAreaData;
import com.team9.questgame.Data.PlayAreaDataSources;
import com.team9.questgame.exception.CardAreaException;
import com.team9.questgame.game_phases.quest.QuestPhaseController;
import com.team9.questgame.gamemanager.service.OutboundService;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class StagePlayAreas implements PlayAreas<AdventureCards>{

    @JsonIgnore
    private final OutboundService outboundService;
    private int stageNum;
    private long id;
    private int battlePoints;
    private int bids;
    @JsonIgnore
    private Logger LOG;
    private HashMap<AllCardCodes, AdventureCards> allCards;
    private QuestCards questCard;
    private QuestPhaseController phaseController;
    private HashMap<AllCardCodes,HashSet<BoostableCard>> cardBoostDependencies;
    private HashSet<BattlePointContributor> cardsWithBattleValue;

    private StagePlayAreas targetPlayArea;




    static private int nextid = 0;

    public StagePlayAreas(QuestCards questCard, int stageNum){
        this.id = nextid++;
        this.questCard = questCard;
        this.stageNum = stageNum;
        //this.questCard = questCard;
        this.allCards = new HashMap<>();
        this.battlePoints = 0;
        this.bids = 0;
        this.outboundService = ApplicationContextHolder.getContext().getBean(OutboundService.class);
        cardBoostDependencies = new HashMap<>();
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
        return false;
    }

    public void onPlayAreaChanges(StagePlayAreas targetPlayArea){
        if(phaseController == null){
            throw new CardAreaException(CardAreaException.CardAreaExceptionReasonCodes.GAMEPHASE_NOT_REGISTERED);
        }
        this.targetPlayArea=targetPlayArea;
    }

    @Override
    public void discardCard(AdventureCards card){

    }

    @Override
    public void onGameReset(){

    }

    @Override
    public int getBattlePoints(){return battlePoints;}

    @Override
    public int getBids(){return bids;}

    @Override
    public void onGamePhaseEnded(){

    }

    public void registerBattlePointContributor(BattlePointContributor card){
        cardsWithBattleValue.add(card);
        updateBattlePoints();
        notifyStageAreaChanged();
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
    }

    public PlayAreaData getPlayAreaData() {
        HashSet<CardTypes> allowedTypes = new HashSet<>();
        allowedTypes.add(CardTypes.FOE);
        allowedTypes.add(CardTypes.WEAPON);
        allowedTypes.add(CardTypes.AMOUR);
        PlayAreaData data = new PlayAreaData(
                PlayAreaDataSources.PLAYER,
                id,
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
     * Updates the clients about a play area being changed, sending the new state.
     */
    private void notifyStageAreaChanged() {
        outboundService.broadcastStageChanged(stageNum,getPlayAreaData());
    }
}

