package com.team9.questgame.Entities.cards;

public class WeaponCards extends AdventureCards {
    private final int bonusBp;

    @Override
    public String toString() {
        return super.toString()+", WeaponCards{" +
                "bonusBp=" + bonusBp +
                '}';
    }

    WeaponCards(Decks assignedDeck, String activeAbilityDescription, String cardName, CardTypes subType, String fileName, AdventureDeckCards cardCode, int bonusBp) {
        super(assignedDeck,activeAbilityDescription, cardName, subType, fileName, cardCode);
        this.bonusBp = bonusBp;
    }

    public int getBonusBp() {
        return bonusBp;
    }

    @Override
    public void playCard() {

    }
}
