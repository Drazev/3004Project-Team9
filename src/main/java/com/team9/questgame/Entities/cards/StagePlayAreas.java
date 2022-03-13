package com.team9.questgame.Entities.cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Data.CardData;
import com.team9.questgame.Data.PlayAreaData;
import com.team9.questgame.Data.PlayAreaDataSources;
import com.team9.questgame.exception.CardAreaException;
import com.team9.questgame.game_phases.quest.QuestPhaseController;
import com.team9.questgame.gamemanager.service.OutboundService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class StagePlayAreas implements PlayAreas<AdventureCards>{

    @JsonIgnore
    private final OutboundService outboundService;
    private long id;
    private int battlePoints;
    private int bids;
    private HashMap<AllCardCodes, AdventureCards> allCards;
    private QuestCards questCard;
    private QuestPhaseController phaseController;

    static private int nextid = 0;

    public StagePlayAreas(){
        this.id = nextid++;
        //this.questCard = questCard;
        this.allCards = new HashMap<>();
        this.battlePoints = 0;
        this.bids = 0;
        this.outboundService = ApplicationContextHolder.getContext().getBean(OutboundService.class);
    }

    @Override
    public boolean receiveCard(AdventureCards card){
        if(card==null) {
            throw new CardAreaException(CardAreaException.CardAreaExceptionReasonCodes.NULL_CARD);
        }
        //TODO
        return false;
    }

    @Override
    public boolean playCard(AdventureCards card){
        return false;
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
}

