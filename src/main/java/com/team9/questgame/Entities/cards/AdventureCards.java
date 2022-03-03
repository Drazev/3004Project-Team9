package com.team9.questgame.Entities.cards;

public abstract class AdventureCards extends Cards {
    protected <T extends Enum<T> & AllCardCodes> AdventureCards(Decks assignedDeck,String activeAbilityDescription, String cardName, CardTypes subType, String fileName, T cardCode) {
        super(assignedDeck,activeAbilityDescription, cardName, subType, "./Assets/Adventure Deck (346x470)/"+fileName, cardCode);
    }

    abstract void playCard();

    @Override
    public String toString() {
        return super.toString()+", AdventureCards{}";
    }
}
