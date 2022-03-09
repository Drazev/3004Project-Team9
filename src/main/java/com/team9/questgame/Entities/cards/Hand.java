package com.team9.questgame.Entities.cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Data.CardData;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.exception.BadRequestException;
import com.team9.questgame.exception.IllegalCardStateException;
import com.team9.questgame.gamemanager.service.OutboundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Hand implements CardArea<AdventureCards> {
    private Logger LOG;
    private final Players player;
    private final PlayerPlayAreas playArea;
    private final OutboundService outboundService;
    boolean isHandOversize;
    private HashSet<AdventureCards> hand;
    private HashMap<Long,AdventureCards> cardIdMap;

    @JsonIgnore
    static public final int MAX_HAND_SIZE = 12;


    public Hand(Players player, PlayerPlayAreas playArea) {
        this.playArea = playArea;
        LOG= LoggerFactory.getLogger(Hand.class);
        this.outboundService = ApplicationContextHolder.getContext().getBean(OutboundService.class);
        this.player=player;
        hand = new HashSet<>();
        cardIdMap = new HashMap<>();
        onGameReset();
    }

    private boolean validateHandSize() {
        boolean prevState=isHandOversize;
        isHandOversize = hand.size()>MAX_HAND_SIZE;
        if(isHandOversize) {
            LOG.debug(player.getName()+": Oversize Hand State SET for "+player.getName());
            notifyHandOversize();
        }
        else if(prevState) {
            LOG.debug(player.getName()+": Oversize Hand State CLEARED for "+player.getName());
        }
        return isHandOversize;
    }

    @Override
    public boolean receiveCard(AdventureCards card) {
        hand.add(card);
        cardIdMap.put(card.getCardID(),card);
        LOG.info(player.getName()+": Has DRAWN a card.");
        validateHandSize();
        player.notifyHandChanged();
        return true;
    }

    @Override
    public void discardCard(AdventureCards card) {
        card.discardCard();
        hand.remove(card);
        LOG.info(player.getName()+": Has DISCARDED a card.");
        validateHandSize();
        player.notifyHandChanged();
    }

    public void discardCard(long cardId) throws BadRequestException,IllegalCardStateException {
        AdventureCards card = findCardFromCardId(cardId);
        discardCard(card);
    }

    @Override
    public boolean playCard(AdventureCards card) {
        LOG.info(player.getName()+": Has PLAYED CARD "+card);
        if(playArea==null || !hand.contains(card)) {
            return false;
        }
        boolean rc = card.playCard(playArea);

        if(rc) {
            hand.remove(card);
            cardIdMap.remove(card.getCardID());
            validateHandSize();
        }

        player.notifyHandChanged();
        return rc;
    }

    public boolean playCard(long cardId) throws BadRequestException,IllegalCardStateException {
        AdventureCards card = findCardFromCardId(cardId);
        return playCard(card);
    }

    @Override
    public void onGameReset() {
        hand.clear();
        cardIdMap.clear();
        isHandOversize=false;
    }

    public ArrayList<CardData> getCardData() {
        ArrayList<CardData> handCards = new ArrayList<>();
        for(Cards card : hand) {
            handCards.add(card.generateCardData());
        }
        return handCards;
    }

    private AdventureCards findCardFromCardId(Long cardId) throws BadRequestException,IllegalCardStateException{
        AdventureCards card = cardIdMap.get(cardId);
        if(card==null) {
            //If we get null, determine if it was bad request or internal error
            if(cardIdMap.containsKey(cardId))
            {
                //The map did not contain the cardId, BAD REQUEST
                throw new BadRequestException("Provided cardId was not found in players hand:  "+player.getName());
            }
            else {
                //The key was mapped to a null card value. This should not happen and is an illegal state
                LOG.error("Player hand state has lost a card in cardIdMap");
                throw new IllegalCardStateException();
            }
        }
        return card;
    }

    void notifyHandOversize() {
        outboundService.sendHandOversize(player.getName());
    }

}
