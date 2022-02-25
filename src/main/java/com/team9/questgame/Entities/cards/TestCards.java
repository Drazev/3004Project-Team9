package com.team9.questgame.Entities.cards;

public class TestCards extends Cards implements AdventureCards {
    private final int minimumBids;
    private final int boostedMinBids;
    private boolean isBoosted;
    private final DeckCards boostConditionCardCode;

    public TestCards(String activeAbilityDescription, String cardName, CardTypes subType, String imageFileName, AdventureDeckCards cardCode, int minimumBids) {
        this(activeAbilityDescription, cardName, subType, imageFileName, cardCode,minimumBids,0,null);
    }

    public TestCards(String activeAbilityDescription, String cardName, CardTypes subType, String imageFileName, AdventureDeckCards cardCode, int minimumBids, int boostedMinBids, DeckCards boostConditionCardCode) {
        super(activeAbilityDescription, cardName, subType, imageFileName, cardCode);
        this.minimumBids = minimumBids;
        this.boostedMinBids = boostedMinBids;
        this.boostConditionCardCode = boostConditionCardCode;
        this.isBoosted=false;
    }

    @Override
    public void playCard() {

    }
}
