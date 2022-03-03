package com.team9.questgame.Entities.cards;

import com.team9.questgame.Data.CardData;

public class TestCards extends AdventureCards {
    private final int minimumBids;
    private final int boostedMinBids;
    private boolean isBoosted;
    private final AllCardCodes boostConditionCardCode;

    public TestCards(Decks assignedDeck,String activeAbilityDescription, String cardName, CardTypes subType, String fileName, AdventureDeckCards cardCode, int minimumBids) {
        this(assignedDeck,activeAbilityDescription, cardName, subType, fileName, cardCode,minimumBids,0,null);
    }

    @Override
    public String toString() {
        return super.toString()+", TestCards{" +
                "minimumBids=" + minimumBids +
                ", boostedMinBids=" + boostedMinBids +
                ", isBoosted=" + isBoosted +
                ", boostConditionCardCode=" + boostConditionCardCode +
                '}';
    }

    public TestCards(Decks assignedDeck, String activeAbilityDescription, String cardName, CardTypes subType, String fileName, AdventureDeckCards cardCode, int minimumBids, int boostedMinBids, AllCardCodes boostConditionCardCode) {
        super(assignedDeck,activeAbilityDescription, cardName, subType, fileName, cardCode);
        this.minimumBids = minimumBids;
        this.boostedMinBids = boostedMinBids;
        this.boostConditionCardCode = boostConditionCardCode;
        this.isBoosted=false;
    }

    @Override
    public void playCard() {

    }

    @Override
    public CardData generateCardData() {
        CardData data = new CardData(
                cardID,
                cardCode,
                cardName,
                subType,
                imgSrc,
                isBoosted ? minimumBids: boostedMinBids,
                0,
                activeAbilityDescription,
                false
        );
        return data;
    }

}
