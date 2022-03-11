package com.team9.questgame.Entities.cards;

import com.team9.questgame.Data.CardData;

public class AmourCards extends AdventureCards implements BattlePointContributor,BidContributor{
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

    @Override
    public String toString() {
        return super.toString()+", AmourCards{" +
                "bonusBp=" + bonusBp +
                ", bids=" + bids +
                '}';
    }

    AmourCards(Decks assignedDeck, String activeAbilityDescription, String cardName, CardTypes subType, String fileName, AdventureDeckCards cardCode) {
        super(assignedDeck,activeAbilityDescription, cardName, subType, fileName, cardCode);
    }

    @Override
    public CardData generateCardData() {
        CardData data = new CardData(
                cardID,
                cardCode,
                cardName,
                subType,
                imgSrc,
                0,
                0,
                activeAbilityDescription,
                false
        );
        return data;
    }

    @Override
    protected void registerWithNewPlayArea(PlayerPlayAreas playArea) {
        playArea.registerBidContributor(this);
        playArea.registerBattlePointContributor(this);
    }

    @Override
    public int getBattlePoints() {
        return bonusBp;
    }

    @Override
    public int getBids() {
        return bids;
    }
}
