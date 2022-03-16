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
import com.team9.questgame.exception.IllegalEffectStateException;
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
    private HashSet<CardWithEffect> activatableCards;

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
        activatableCards = new HashSet<>();
        cardIdMap = new HashMap<>();
        onGameReset();
    }

    public boolean isHandOversize() {
        return isHandOversize;
    }

    public int getHandSize() {
        return hand.size();
    }

    public HashMap<CardTypes,HashMap<AllCardCodes,Integer>> getNumberOfEachCardCodeBySubType() {
        HashMap<CardTypes,HashMap<AllCardCodes,Integer>> cardList = new HashMap<>();

        for(AdventureCards card : hand) {
            HashMap<AllCardCodes,Integer> cardMap;
            CardTypes type = card.getSubType();
            int numCards=0;
            if(!cardList.containsKey(type)) {
                cardMap = new HashMap<>();
                cardList.put(type,cardMap);
            }
            else {
                cardMap = cardList.get(type);
            }

            if(cardMap.containsKey(card.getCardCode()))
            {
                numCards = cardMap.get(card.getCardCode());
            }
            ++numCards;
            cardMap.put(card.getCardCode(),numCards);
        }

        return cardList;
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
        LOG.info(player.getName()+": Has received card"+card.getCardCode());
        hand.add(card);
        cardIdMap.put(card.getCardID(),card);
        validateHandSize();
        notifyHandUpdated();
        return true;
    }

    @Override
    public void discardCard(AdventureCards card) throws CardAreaException {
        if(!playArea.isPlayersTurn() && !isHandOversize) {
            throw new CardAreaException("Card {"+card.getCardCode()+","+card.getSubType()+"} cannot be DISCARDED at this time.", CardAreaException.CardAreaExceptionReasonCodes.RULE_VIOLATION_CANNOT_PLAY_OR_DISCARD_OUT_OF_TURN);
        }
        card.discardCard();
        hand.remove(card);
        cardIdMap.remove(card.getCardID());
        LOG.info(player.getName()+": Has DISCARDED card "+card.getCardCode());
        validateHandSize();
        notifyHandUpdated();
    }

    public void discardCard(long cardId) throws BadRequestException,IllegalCardStateException {
        AdventureCards card = findCardFromCardId(cardId);
        discardCard(card);
    }

    @Override
    public boolean playCard(AdventureCards card) throws CardAreaException {
        if(playArea==null || !hand.contains(card)) {
            return false;
        }
        boolean rc = card.playCard(playArea);

        if(rc) {
            hand.remove(card);
            cardIdMap.remove(card.getCardID());
            activatableCards.remove(card);
            validateHandSize();
            LOG.info(player.getName()+": Has PLAYED CARD "+card.getCardCode());
        }

        notifyHandUpdated();
        return rc;
    }

    public boolean playCard(long cardId) throws BadRequestException,IllegalCardStateException {
        AdventureCards card = findCardFromCardId(cardId);
        return playCard(card);
    }

    public boolean activateCard(long cardId) throws CardAreaException, IllegalEffectStateException {
        boolean rc = false;
        if(!cardIdMap.containsKey(cardId)) {
            rc=playArea.activateCard(cardId);
        }
        else {
            for(CardWithEffect card : activatableCards) {
                if(card.getCardID()==cardId) {
                    card.activate(player);
                    card.discardCard();
                    activatableCards.remove(card);
                    rc=true;
                }
            }
        }
        return rc;
    }

    @Override
    public void onGameReset() {
        hand.clear();
        cardIdMap.clear();
        activatableCards.clear();
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

    public void registerCardWithEffect(CardWithEffect card) {
        activatableCards.add(card);
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
