package com.team9.questgame.Entities.cards;

public abstract class StoryCards extends Cards {

    protected <T extends Enum<T> & AllCardCodes> StoryCards(Decks assignedDeck,String activeAbilityDescription, String cardName, CardTypes subType, String fileName, T cardCode) {
        super(assignedDeck,activeAbilityDescription, cardName, subType, "./Assets/Story Deck (327x491)/"+fileName, cardCode);
    }

    @Override
    public String toString() {
        return super.toString()+", StoryCards{}";
    }
}
