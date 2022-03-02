package com.team9.questgame.Entities.cards;

public class FoeCards extends AdventureCards {
    private final int bpValue;
    private final int boostedBpValue;
    private boolean isBoosted;

    @Override
    public String toString() {
        return super.toString()+", FoeCards{" +
                "bpValue=" + bpValue +
                ", boostedBpValue=" + boostedBpValue +
                ", isBoosted=" + isBoosted +
                '}';
    }

    public FoeCards(Decks assignedDeck, String activeAbilityDescription, String cardName, CardTypes subType, String imageFileName, AdventureDeckCards cardCode, int battlePointValue) {
        this(assignedDeck,activeAbilityDescription, cardName, subType, imageFileName, cardCode,battlePointValue,0);
    }
    public FoeCards(Decks assignedDeck,String activeAbilityDescription, String cardName, CardTypes subType, String imageFileName, AdventureDeckCards cardCode, int battlePointValue, int boostedBattlePointValue) {
        super(assignedDeck,activeAbilityDescription, cardName, subType, imageFileName, cardCode);
        this.bpValue=battlePointValue;
        this.boostedBpValue=boostedBattlePointValue;
        this.isBoosted=false;
    }


    @Override
    public void playCard()
    {

    }
}
