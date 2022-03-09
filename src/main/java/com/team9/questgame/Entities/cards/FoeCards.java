package com.team9.questgame.Entities.cards;

import com.team9.questgame.Data.CardData;
import com.team9.questgame.Entities.Effects.Effects;

public class FoeCards extends AdventureCards implements BoostableCard, BattlePointContributor {
    private final int bpValue;
    private final int boostedBpValue;
    private boolean isBoosted;
    private Effects activeEffect; //TODO: Modify for Effect implementation

    @Override
    public String toString() {
        return super.toString()+", FoeCards{" +
                "bpValue=" + bpValue +
                ", boostedBpValue=" + boostedBpValue +
                ", isBoosted=" + isBoosted +
                '}';
    }

    @Override
    public void notifyBoostEnded(CardArea boostTriggerLocation) {
        if(boostTriggerLocation==location) {
            isBoosted=false;
        }
    }

    public void setBoosted(boolean boosted) {
        isBoosted = boosted;
    }

    public int getBattlePoints() {
        return isBoosted ? bpValue : boostedBpValue;
    }

    public boolean isBoosted() {
        return isBoosted;
    }

    public Effects getActiveEffect() {
        return activeEffect;
    }

    public FoeCards(Decks assignedDeck, String activeAbilityDescription, String cardName, CardTypes subType, String imageFileName, AdventureDeckCards cardCode, int battlePointValue) {
        this(assignedDeck,activeAbilityDescription, cardName, subType, imageFileName, cardCode,battlePointValue,0);
    }
    public FoeCards(Decks assignedDeck,String activeAbilityDescription, String cardName, CardTypes subType, String imageFileName, AdventureDeckCards cardCode, int battlePointValue, int boostedBattlePointValue) {
        super(assignedDeck,activeAbilityDescription, cardName, subType, imageFileName, cardCode);
        this.bpValue=battlePointValue;
        this.boostedBpValue=boostedBattlePointValue;
        this.isBoosted=false;
        this.activeEffect=null; //TODO: Change when Effects Implemented
    }

    @Override
    public CardData generateCardData() {
        CardData data = new CardData(
                cardID,
                cardCode,
                cardName,
                subType,
                imgSrc,
                0,
                isBoosted ? bpValue : boostedBpValue,
                activeAbilityDescription,
                activeEffect!=null
        );
        return data;
    }

    @Override
    boolean playCard(PlayAreas cardArea) {
        boolean rc=super.playCard(cardArea);
        //Based on card attributes, register with PlayArea
        if(rc) {
            if(activeEffect!=null) {
                location.registerActiveEffect(this);
            }

            if(bpValue>0 || boostedBpValue>0) {
                location.registerBattlePointContributor(this);
            }
        }
        return rc;
    }

    @Override
    public boolean discardCard() {
        PlayAreas oldLocation=location;
        boolean rc=  super.discardCard();
        if(rc) {
            isBoosted=false;
        }

        if(activeEffect!=null) {
            oldLocation.removeActiveEffect(this);
        }

        if(bpValue>0 || boostedBpValue>0) {
            oldLocation.removeBattlePointContributor(this);
        }

        return rc;
    }
}
