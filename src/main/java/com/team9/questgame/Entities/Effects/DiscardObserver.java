package com.team9.questgame.Entities.Effects;

import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.CardTypes;
import com.team9.questgame.Entities.cards.Cards;

import java.util.HashMap;

public class DiscardObserver {
    private final Effects effect;
    private final Players targetPlayer;
    private final HashMap<CardTypes,Integer> originalList;
    private HashMap<CardTypes,Integer> discardList;
    private final int numCardsToDiscard;
    private int numDiscarded;
    private boolean isResolved;

    public Effects getEffect() {
        return effect;
    }

    public Players getTargetPlayer() {
        return targetPlayer;
    }

    public HashMap<CardTypes, Integer> getOriginalList() {
        return originalList;
    }

    public HashMap<CardTypes, Integer> getDiscardList() {
        return discardList;
    }

    public DiscardObserver(Effects effect, Players targetPlayer, HashMap<CardTypes,Integer> discardList) {
        this.effect=effect;
        this.targetPlayer=targetPlayer;
        this.originalList = discardList;
        this.discardList=new HashMap<>(discardList);
        this.numCardsToDiscard=0;
        this.numDiscarded=0;
    }

    public DiscardObserver(Effects effect, Players targetPlayer,int numCardsToDiscard) {
        this.effect=effect;
        this.targetPlayer=targetPlayer;
        this.originalList = null;
        this.discardList= null;
        this.numCardsToDiscard=numCardsToDiscard;
        this.numDiscarded=0;
        this.isResolved=false;
    }

    public void notifyCardDiscarded(Cards card) {
        if(card==null || isResolved) {
            return;
        }
        updateDiscardedList(card);
        ++numDiscarded;

        if(discardList!=null) {
            if(discardList.isEmpty()) {
                isResolved=true;
                EffectResolverService.getService().onDiscardObserverResolution(this);
            }
        }
        else if(numCardsToDiscard>0 && numDiscarded >=numCardsToDiscard) {
            isResolved=true;
            EffectResolverService.getService().onDiscardObserverResolution(this);
        }
    }

    private void updateDiscardedList(Cards card) {
        if(discardList!=null && discardList.containsKey(card.getSubType())) {
            int c = discardList.get(card.getSubType());
            --c;
            if(c<1) {
                discardList.remove(card.getSubType());
            }
            else {
                discardList.put(card.getSubType(),c);
            }
        }
    }
}
