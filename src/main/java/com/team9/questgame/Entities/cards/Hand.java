package com.team9.questgame.Entities.cards;

import com.fasterxml.jackson.annotation.*;
import com.team9.questgame.Data.CardData;
import com.team9.questgame.Data.HandData;
import com.team9.questgame.Data.HandOversizeData;
import com.team9.questgame.Entities.Effects.DiscardObserver;
import com.team9.questgame.Entities.Effects.DiscardSubject;
import com.team9.questgame.Entities.Effects.EffectObserver;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.exception.BadRequestException;
import com.team9.questgame.exception.CardAreaException;
import com.team9.questgame.exception.IllegalCardStateException;
import com.team9.questgame.exception.IllegalEffectStateException;
import com.team9.questgame.gamemanager.service.InboundService;
import com.team9.questgame.gamemanager.service.OutboundService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property="handID"
)
public class Hand implements CardArea<AdventureCards>, EffectObserver<AdventureCards>, DiscardSubject {
    @JsonIgnore
    private final Logger LOG;
    private final long handID;
    static private long nextid=0;
    @JsonIgnore
    private final Players player;
    @JsonIgnore
    private final PlayerPlayAreas playArea;

    private boolean isHandOversize;

    @Getter
    private final HashSet<AdventureCards> hand;
    private final HashSet<CardWithEffect> activatableCards;
    private final HashSet<DiscardObserver> discardObservers;

    private final HashMap<Long,AdventureCards> cardIdMap;

    @JsonIgnore
    static public final int MAX_HAND_SIZE = 12;


    public Hand(Players player, PlayerPlayAreas playArea) {
        this.handID =nextid++;
        this.playArea = playArea;
        LOG= LoggerFactory.getLogger(Hand.class);
        this.player=player;
        hand = new HashSet<>();
        activatableCards = new HashSet<>();
        cardIdMap = new HashMap<>();
        discardObservers = new HashSet<>();
    }

    public boolean isHandOversize() {
        return isHandOversize;
    }

    public int getHandSize() {
        return hand.size();
    }


    public long getHandId() {
        return handID;
    }

    public HashMap<CardTypes,HashMap<AllCardCodes<AdventureDeckCards>,Integer>> getNumberOfEachCardCodeBySubType() {
        HashMap<CardTypes,HashMap<AllCardCodes<AdventureDeckCards>,Integer>> cardList = new HashMap<>();

        for(AdventureCards card : hand) {
            HashMap<AllCardCodes<AdventureDeckCards>,Integer> cardMap;
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

    public boolean discardCard(long cardId) throws BadRequestException,IllegalCardStateException {
        AdventureCards card = cardIdMap.get(cardId);
        if(card==null) {
            return false;
        }
        discardCard(card);
        notifyDiscardObservers(card);
        return true;
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
        AdventureCards card = cardIdMap.get(cardId);
        if(card==null) {
            return false;
        }
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
                    card.activate(this,player);
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
        discardObservers.clear();
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

    public ArrayList<CardData> generateObfuscatedCardData() {
        ArrayList<CardData> handCards = new ArrayList<>(); //TODO: Switch to only hidden cards
        for(Cards card : hand) {
            handCards.add(card.generateObfuscatedCardData());
        }
        return handCards;
    }

    public HandData generateHandData() {
        return new HandData(
                player.getPlayerId(),
                handID,
                player.getName(),
                isHandOversize,
                generateCardData()
        );
    }

    public HandData generateObfuscatedHandData() {
        return new HandData(
                player.getPlayerId(),
                handID,
                player.getName(),
                isHandOversize,
                generateObfuscatedCardData()
        );
    }

    public void registerCardWithEffect(CardWithEffect card) {
        activatableCards.add(card);
    }

    private void notifyHandOversize() {
        HandOversizeData data = new HandOversizeData(player.getPlayerId(),MAX_HAND_SIZE,getHandSize());
        InboundService.getService().playerNotifyHandOversize();
        OutboundService.getService().broadcastHandOversize(player,data);
    }

    private void notifyHandUpdated() {
        OutboundService.getService().broadcastHandUpdate(player,generateHandData(),generateObfuscatedHandData());
    }

    @Override
    public void onEffectResolved(CardWithEffect resolvedCard) {
        if(hand.contains((AdventureCards) resolvedCard)) {
            AdventureCards card = (AdventureCards) resolvedCard;
            card.discardCard();
            activatableCards.remove(card);
        }
        else {
            LOG.warn("Hand::onEffectResolved returned a CardWithEffect that was not in the play area!, "+resolvedCard.getCardCode());
        }
    }

    @Override
    public void onEffectResolvedWithDelayedTrigger(CardWithEffect resolvedCard) {
        //Not used
    }

    @Override
    public void registerDiscardObserver(DiscardObserver observer) {
        discardObservers.add(observer);
    }

    @Override
    public void unregisterDiscardObserver(DiscardObserver observer) {
        discardObservers.remove(observer);
    }

    @Override
    public void notifyDiscardObservers(Cards card) {
        for(DiscardObserver o : discardObservers) {
            o.notifyCardDiscarded(card);
        }
    }
}
