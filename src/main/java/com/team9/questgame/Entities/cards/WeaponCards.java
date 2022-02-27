package com.team9.questgame.Entities.cards;

public class WeaponCards extends AdventureCards {
    private final int bonusBp;

    WeaponCards(Decks assignedDeck,String activeAbilityDescription, String cardName, CardTypes subType, String imgSrc, AdventureDeckCards cardCode, int bonusBp) {
        super(assignedDeck,activeAbilityDescription, cardName, subType, imgSrc, cardCode);
        this.bonusBp = bonusBp;
    }

    public int getBonusBp() {
        return bonusBp;
    }

    @Override
    public void playCard() {

    }
}
