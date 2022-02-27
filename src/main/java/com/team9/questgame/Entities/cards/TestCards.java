package com.team9.questgame.Entities.cards;

public class TestCards extends AdventureCards {
    private final int minimumBids;
    private final int boostedMinBids;
    private boolean isBoosted;
    private final AllCardCodes boostConditionCardCode;

    public TestCards(Decks assignedDeck,String activeAbilityDescription, String cardName, CardTypes subType, String imgSrc, AdventureDeckCards cardCode, int minimumBids) {
        this(assignedDeck,activeAbilityDescription, cardName, subType, imgSrc, cardCode,minimumBids,0,null);
    }

    public TestCards(Decks assignedDeck,String activeAbilityDescription, String cardName, CardTypes subType, String imgSrc, AdventureDeckCards cardCode, int minimumBids, int boostedMinBids, AllCardCodes boostConditionCardCode) {
        super(assignedDeck,activeAbilityDescription, cardName, subType, imgSrc, cardCode);
        this.minimumBids = minimumBids;
        this.boostedMinBids = boostedMinBids;
        this.boostConditionCardCode = boostConditionCardCode;
        this.isBoosted=false;
    }

    @Override
    public void playCard() {

    }
}
