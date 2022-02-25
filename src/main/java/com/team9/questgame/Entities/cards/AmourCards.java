package com.team9.questgame.Entities.cards;

public class AmourCards extends Cards implements AdventureCards {
    private final int bonusBp=10;
    private final int bids=1;

    AmourCards() {
        super(
                null,
                "Amour",
                AdventureDeckCards.AMOUR.getSubType(),
                "Amour.png",
                AdventureDeckCards.AMOUR
        );
    }
    AmourCards(String activeAbilityDescription, String cardName, CardTypes subType, String imageFileName, AdventureDeckCards cardCode) {
        super(activeAbilityDescription, cardName, subType, imageFileName, cardCode);
    }

    @Override
    public void playCard() {

    }
}
