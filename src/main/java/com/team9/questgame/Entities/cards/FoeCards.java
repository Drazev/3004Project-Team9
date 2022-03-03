package com.team9.questgame.Entities.cards;

import com.team9.questgame.Data.CardData;
import com.team9.questgame.Entities.Effects.Effects;

public class FoeCards extends AdventureCards {
    private final int bpValue;
    private final int boostedBpValue;
    private boolean isBoosted;
    private Effects activeEffect; //TODO: Modify for Effect implementation

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
        this.activeEffect=null; //TODO: Change when Effects Implemented
    }


    @Override
    public void playCard()
    {

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
                isBoosted ? bpValue : boostedBpValue,
                activeAbilityDescription,
                activeEffect!=null
        );
        return data;
    }
}
