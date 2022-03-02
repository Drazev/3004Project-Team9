package com.team9.questgame.Entities.cards;

public abstract class StoryCards extends Cards {

    protected <T extends Enum<T> & AllCardCodes> StoryCards(Decks assignedDeck,String activeAbilityDescription, String cardName, CardTypes subType, String imgSrc, T cardCode) {
        super(assignedDeck,activeAbilityDescription, cardName, subType, imgSrc, cardCode);
    }

    @Override
    public String toString() {
        return super.toString()+", StoryCards{}";
    }
}
