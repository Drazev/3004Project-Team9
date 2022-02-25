package com.team9.questgame.Entities.cards;

public class WeaponCards extends Cards implements AdventureCards {
    private final int bonusBp;

    WeaponCards(String activeAbilityDescription, String cardName, CardTypes subType, String imageFileName, AdventureDeckCards cardCode, int bonusBp) {
        super(activeAbilityDescription, cardName, subType, imageFileName, cardCode);
        this.bonusBp = bonusBp;
    }

    public int getBonusBp() {
        return bonusBp;
    }

    @Override
    public void playCard() {

    }
}
