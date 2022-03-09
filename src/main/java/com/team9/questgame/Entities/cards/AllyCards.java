package com.team9.questgame.Entities.cards;

import com.team9.questgame.Data.CardData;
import com.team9.questgame.Entities.Effects.Effects;

/**
 * Entity representing an Ally card within the game.
 *
 * Allies may have conditional effects based on a target card being present in a particular area.
 * An Allies boost effect can either be triggered by a particular Quest card being in play, or by a specific
 * ally being in the owing players play area.
 * @param <T> This is an enumeration type representing the card that triggers the boost.
 */
public class AllyCards <T extends Enum<T> & AllCardCodes> extends AdventureCards implements BoostableCard, BattlePointContributor,BidContributor {
    private final int bonusBp; //Battle Points
    private final int bids;
    private final int boostBonusBp;
    private final int boostBids;
    boolean isBoosted;
    private final T boostConditionCardCode;
    private Effects activeEffect; //TODO: Modify for Effect implementation

    /**
     *
     * @param activeAbilityDescription A description text to appear at the bottom of a card with an active or conditional effect.
     * @param cardName The title of the card, appearing at the top.
     * @param subType The subtype of card within it's deck type. This is generally Foe,Ally,etc..
     * @param fileName The uri path where the image representing the card can be found from the client.
     * @param cardCode An enumeration cardCode that helps identify which unique card this instance represents. Many cards will have multiple copies in a deck.
     * @param bonusBp The battlepoints this ally contributes
     * @param bids The bids this ally contributes
     */
    public AllyCards(Decks assignedDeck,String activeAbilityDescription, String cardName, CardTypes subType, String fileName, AdventureDeckCards cardCode, int bonusBp, int bids) {
        this(assignedDeck,activeAbilityDescription, cardName, subType, fileName, cardCode,bonusBp,bids,0,0,null);
    }

    /**
     *
     * @param activeAbilityDescription A description text to appear at the bottom of a card with an active or conditional effect.
     * @param cardName The title of the card, appearing at the top.
     * @param subType The subtype of card within it's deck type. This is generally Foe,Ally,etc..
     * @param fileName The uri path where the image representing the card can be found from the client.
     * @param cardCode An enumeration cardCode that helps identify which unique card this instance represents. Many cards will have multiple copies in a deck.
     * @param bonusBp The battlepoints this ally contributes
     * @param bids The bids this ally contributes
     * @param boostBonusBp The total number of battlepoints this ally contributes if the boost condition was met.
     * @param boostBids The total number of bids this ally contributes if the boost condition was met.
     * @param boostConditionCardCode The target card that will trigger this ally's boost effect.
     */
    public AllyCards(Decks assignedDeck,String activeAbilityDescription, String cardName, CardTypes subType, String fileName, AdventureDeckCards cardCode, int bonusBp, int bids, int boostBonusBp, int boostBids,T boostConditionCardCode) {
        super(assignedDeck,activeAbilityDescription, cardName, subType, fileName, cardCode);
        this.bonusBp = bonusBp;
        this.bids = bids;
        this.boostBonusBp = boostBonusBp;
        this.boostBids=boostBids;
        this.isBoosted=false;
        this.boostConditionCardCode = boostConditionCardCode;
        this.activeEffect=null; //TODO: Change when Effects implemented

    }

    @Override
    public String toString() {
        return super.toString() + ", AllyCards{" +
                "bonusBp=" + bonusBp +
                ", bids=" + bids +
                ", boostBonusBp=" + boostBonusBp +
                ", boostBids=" + boostBids +
                ", isBoosted=" + isBoosted +
                ", boostCardCode=" + boostConditionCardCode +
                '}';
    }

    @Override
    public void notifyBoostEnded(CardArea boostTriggerLocation) {
        if(boostTriggerLocation==location) {
            isBoosted=false;
        }
    }

    @Override
    public CardData generateCardData() {
        CardData data = new CardData(
                cardID,
                cardCode,
                cardName,
                subType,
                imgSrc,
                isBoosted ? bids : boostBids,
                isBoosted ? bonusBp : boostBonusBp,
                activeAbilityDescription,
                activeEffect!=null
        );
        return data;
    }

    public int getBids() {
        return isBoosted ? bids : boostBids;
    }

    public int getBattlePoints() {
        return isBoosted ? bonusBp : boostBonusBp;
    }

    public boolean isBoosted() {
        return isBoosted;
    }

    public void setBoosted(boolean boosted) {
        isBoosted = boosted;
    }

    public T getBoostConditionCardCode() {
        return boostConditionCardCode;
    }

    public Effects getActiveEffect() {
        return activeEffect;
    }

    boolean playCard(PlayAreas playArea) {
        boolean rc=super.playCard(playArea);

        //Based on card attributes, register with PlayArea
        if(rc) {
            if(activeEffect!=null) {
                location.registerActiveEffect(this);
            }

            if(bids>0 || boostBids>0) {
                location.registerBidContributor(this);
            }

            if(bonusBp>0 || boostBonusBp>0) {
                location.registerBattlePointContributor(this);
            }

            if(boostConditionCardCode!=null) {
                location.registerCardBoostDependency(boostConditionCardCode,this);
            }
        }


        return rc;
    }

    @Override
    public boolean discardCard() {
        PlayAreas oldLocation = location;
        boolean rc=  super.discardCard();
        if(rc) {
            isBoosted=false;

            if(activeEffect!=null) {
                oldLocation.removeActiveEffect(this);
            }

            if(bids>0 || boostBids>0) {
                oldLocation.removeBidContributor(this);
            }

            if(bonusBp>0 || boostBonusBp>0) {
                oldLocation.removeBattlePointContributor(this);
            }

            if(boostConditionCardCode!=null) {
                location.removeCardBoostDependency(boostConditionCardCode,this);
            }
        }
        return rc;
    }
}
