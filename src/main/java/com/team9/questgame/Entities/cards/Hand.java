package com.team9.questgame.Entities.cards;

import com.fasterxml.jackson.annotation.*;
import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Data.CardData;
import com.team9.questgame.Data.HandData;
import com.team9.questgame.Data.HandOversizeData;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.exception.BadRequestException;
import com.team9.questgame.exception.CardAreaException;
import com.team9.questgame.exception.IllegalCardStateException;
import com.team9.questgame.gamemanager.service.InboundService;
import com.team9.questgame.gamemanager.service.OutboundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property="id"
)
public class Hand implements CardArea<AdventureCards> {
    @JsonIgnore
    private Logger LOG;
    private long id;
    static private long nextid=0;
    @JsonIgnore
    private final Players player;
    @JsonIgnore
    private final PlayerPlayAreas playArea;

    @JsonIgnore
    private final OutboundService outboundService;
    @JsonIgnore
    private final InboundService inboundService;

    private boolean isHandOversize;

    private HashSet<AdventureCards> hand;

    private HashMap<Long,AdventureCards> cardIdMap;

    @JsonIgnore
    static public final int MAX_HAND_SIZE = 12;


    public Hand(Players player, PlayerPlayAreas playArea) {
        this.id=nextid++;
        this.playArea = playArea;
        LOG= LoggerFactory.getLogger(Hand.class);
        this.outboundService = ApplicationContextHolder.getContext().getBean(OutboundService.class);
        this.inboundService = ApplicationContextHolder.getContext().getBean(InboundService.class);
        this.player=player;
        hand = new HashSet<>();
        cardIdMap = new HashMap<>();
        onGameReset();
    }

    public boolean isHandOversize() {
        return isHandOversize;
    }

    public int getHandSize() {
        return hand.size();
    }

    public HashMap<CardTypes,HashSet<AllCardCodes>> getUniqueCardsCodesBySubType() {
        HashMap<CardTypes,HashSet<AllCardCodes>> uniqueCards = new HashMap<>();

        for(AdventureCards card : hand) {
            HashSet<AllCardCodes> codes = uniqueCards.get(card.getSubType());
            if(codes==null) {
                codes = new HashSet<>();
                uniqueCards.put(card.getSubType(),codes);
            }
            codes.add(card.getCardCode());
        }

        return uniqueCards;
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
        notifyHandUpdated();
        return true;
    }

    @Override
    public void discardCard(AdventureCards card) {
        card.discardCard();
        hand.remove(card);
        cardIdMap.remove(card.getCardID());
        LOG.info(player.getName()+": Has DISCARDED a card.");
        validateHandSize();
        notifyHandUpdated();
    }

    public void discardCard(long cardId) throws BadRequestException,IllegalCardStateException {
        AdventureCards card = findCardFromCardId(cardId);
        discardCard(card);
    }

    @Override
    public boolean playCard(AdventureCards card) throws CardAreaException {
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

        notifyHandUpdated();
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
        notifyHandUpdated();
    }

    public ArrayList<CardData> generateCardData() {
        ArrayList<CardData> handCards = new ArrayList<>();
        for(Cards card : hand) {
            handCards.add(card.generateCardData());
        }
        return handCards;
    }

    public HandData generateHandData() {
        return new HandData(
                player.getPlayerId(),
                player.getName(),
                isHandOversize,
                generateCardData()
        );
    }

    private AdventureCards findCardFromCardId(Long cardId) throws BadRequestException,IllegalCardStateException{
        AdventureCards card = cardIdMap.get(cardId);
        if(card==null) {
            //If we get null, determine if it was bad request or internal error
            if(!cardIdMap.containsKey(cardId))
            {
                //The map did not contain the cardId, BAD REQUEST
                throw new BadRequestException("Provided cardId was not found in players hand:  "+player.getName());
            }
        }
        return card;
    }

    private void notifyHandOversize() {
        HandOversizeData data = new HandOversizeData(player.getPlayerId(),MAX_HAND_SIZE,getHandSize());
        inboundService.playerNotifyHandOversize();
        outboundService.broadcastHandOversize(player,data);
    }

    private void notifyHandUpdated() {
        outboundService.broadcastHandUpdate(generateHandData());
    }

}
