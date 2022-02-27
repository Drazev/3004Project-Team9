package com.team9.questgame.Entities.cards;

public class AmourCards extends AdventureCards {
    private final int bonusBp=10;
    private final int bids=1;

    AmourCards(Decks assignedDeck,String imgSrc) {
        super(
                assignedDeck,
                null,
                "Amour",
                AdventureDeckCards.AMOUR.getSubType(),
                imgSrc,
                AdventureDeckCards.AMOUR
        );
    }
    AmourCards(Decks assignedDeck,String activeAbilityDescription, String cardName, CardTypes subType, String imgSrc, AdventureDeckCards cardCode) {
        super(assignedDeck,activeAbilityDescription, cardName, subType, imgSrc, cardCode);
    }

    @Override
    public void playCard() {

    }
}
