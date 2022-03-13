package com.team9.questgame.Entities.cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.exception.CardAreaException;
import com.team9.questgame.gamemanager.service.OutboundService;

import java.util.HashMap;

public class StagePlayAreas implements PlayAreas<AdventureCards>{

    @JsonIgnore
    private final OutboundService outboundService;
    private String id;
    private int battlePoints;
    private int bids;
    private HashMap<AllCardCodes, AdventureCards> allCards;
    private QuestCards questCard;

    static private int nextid = 0;

    public StagePlayAreas(){
        this.id = "q"+nextid++;
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
}

