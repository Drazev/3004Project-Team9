package com.team9.questgame.Entities.cards;

import java.util.Objects;

public abstract class Cards {
    protected final Decks assignedDeck;
    protected String activeAbilityDescription;
    protected String cardName;
    protected long cardID;
    protected final CardTypes subType;
    protected String imgSrc;
    protected static long nextId=0;
    protected AllCardCodes cardCode;

    protected <T extends Enum<T> & AllCardCodes> Cards(Decks assignedDeck,String activeAbilityDescription, String cardName, CardTypes subType, String imgSrc, T cardCode) {
        this.cardID = nextId++;

        this.assignedDeck = assignedDeck;
        this.activeAbilityDescription = activeAbilityDescription;
        this.cardName = cardName;
        this.subType = subType;
        this.imgSrc = imgSrc;
        this.cardCode = cardCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cards)) return false;
        Cards cards = (Cards) o;
        return cardID == cards.cardID && cardCode==cards.cardCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardID);
    }

    public boolean discardCard() {
        boolean rc=false;


        //notify assigned deck that card was discarded
        assignedDeck.notifyDiscard(this);

        return rc;
    }

}
