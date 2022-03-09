package com.team9.questgame.Entities.cards;

import com.team9.questgame.Data.CardData;
import com.team9.questgame.exception.IllegalCardStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public abstract class Cards {
    protected Logger LOG;
    protected final Decks assignedDeck;
    protected String activeAbilityDescription;
    protected String cardName;
    protected long cardID;
    protected final CardTypes subType;
    protected String imgSrc;
    protected static long nextId=0;
    protected AllCardCodes cardCode;
    protected PlayAreas location;

    protected <T extends Enum<T> & AllCardCodes> Cards(Decks assignedDeck,String activeAbilityDescription, String cardName, CardTypes subType, String imgSrc, T cardCode) {
        this.cardID = nextId++;

        LOG= LoggerFactory.getLogger(this.getClass());
        this.assignedDeck = assignedDeck;
        this.activeAbilityDescription = activeAbilityDescription;
        this.cardName = cardName;
        this.subType = subType;
        this.imgSrc = imgSrc;
        this.cardCode = cardCode;
    }

    public String getCardName() {
        return cardName;
    }

    public CardTypes getSubType() {
        return subType;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public AllCardCodes getCardCode() {
        return cardCode;
    }

    public CardArea getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cards)) return false;
        Cards cards = (Cards) o;
        return cardID == cards.cardID && cardCode==cards.cardCode;
    }

    public long getCardID() {
        return cardID;
    }

    @Override
    public String toString() {
        return "Cards{" +
                "assignedDeck=" + assignedDeck +
                ", activeAbilityDescription='" + activeAbilityDescription + '\'' +
                ", cardName='" + cardName + '\'' +
                ", cardID=" + cardID +
                ", subType=" + subType +
                ", imgSrc='" + imgSrc + '\'' +
                ", cardCode=" + cardCode +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardID);
    }

    public boolean discardCard() {
        boolean rc=false;
        CardArea oldLocation = location;
        location=null;
        //notify assigned deck that card was discarded
        assignedDeck.notifyDiscard(this);
        onLocationChanged(oldLocation);

        return rc;
    }

    abstract public CardData generateCardData();

    boolean playCard(PlayAreas cardArea) {
        if(location==cardArea)
        {
            throw new IllegalCardStateException("Card cannot be played into the same area.");
        }
        else if(cardArea == null) {
            throw new IllegalCardStateException("Card cannot be played to null. Card must be played into another card area or discard.");
        }

        if(cardArea.receiveCard(this)) {
            CardArea oldLocation=location;
            location = cardArea;
            onLocationChanged(oldLocation);

            return true;
        }
        return false;
    }


    abstract protected void onLocationChanged(CardArea oldLocation);

}
