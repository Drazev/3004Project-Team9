package com.team9.questgame.Entities.cards;

import com.team9.questgame.Data.CardData;
import com.team9.questgame.Entities.Effects.CardEffects.TestEndEffect;
import com.team9.questgame.Entities.Effects.EffectObserver;
import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Players;

public class TestCards <T extends Enum<T> & AllCardCodes> extends AdventureCards implements BoostableCard, CardWithEffect<TestCards> {
    private final int minimumBids;
    private final int boostedMinBids;
    private boolean isBoosted;
    private final T boostConditionCardCode;
    private TestEndEffect testEndEffect;

    public TestCards(Decks assignedDeck,String activeAbilityDescription, String cardName, CardTypes subType, String fileName, AdventureDeckCards cardCode, int minimumBids) {
        this(assignedDeck,activeAbilityDescription, cardName, subType, fileName, cardCode,minimumBids,0,null);
    }

    public TestCards(Decks assignedDeck, String activeAbilityDescription, String cardName, CardTypes subType, String fileName, AdventureDeckCards cardCode, int minimumBids, int boostedMinBids, T boostConditionCardCode) {
        super(assignedDeck,activeAbilityDescription, cardName, subType, fileName, cardCode);
        this.minimumBids = minimumBids;
        this.boostedMinBids = boostedMinBids;
        this.boostConditionCardCode = boostConditionCardCode;
        this.isBoosted=false;
    }

    public int getMinimumBids() {
        if(minimumBids<boostedMinBids & isBoosted)
        {
            return boostedMinBids;
        }
        return minimumBids;
    }

    public boolean isBoosted() {
        return isBoosted;
    }

    public void setBoosted(boolean boosted) {
        isBoosted = boosted;
    }

    @Override
    public void notifyBoostEnded(CardArea boostTriggerLocation) {
        if(boostTriggerLocation==location) {
            isBoosted=false;
        }
    }

    public T getBoostConditionCardCode() {
        return boostConditionCardCode;
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

    @Override
    public CardData generateCardData() {
        CardData data = new CardData(
                cardID,
                cardCode,
                cardName,
                subType,
                imgSrc,
                getMinimumBids(),
                0,
                activeAbilityDescription,
                false
        );
        return data;
    }

    @Override
    protected void registerWithNewPlayerPlayArea(PlayerPlayAreas playArea) {
        if(boostConditionCardCode!=null) {
            playArea.registerCardBoostDependency(boostConditionCardCode,this);
        }
    }

    @Override
    protected void registerwithNewPlayArea(PlayAreas playArea) {

        playArea.registerBoostableCard(this);
        playArea.registerMinBid(this);
    }

    public void setEffect(TestEndEffect effect){
        this.testEndEffect = effect;
    }

    @Override
    public TestCards getCard() {
        return this;
    }

    @Override
    public void discardCard() {
        isBoosted=false;
        super.discardCard();
    }

    @Override
    public void activate(EffectObserver observer, Players activatingPlayer) {
        testEndEffect.activate(observer, activatingPlayer);
    }

}
