package com.team9.questgame.Entities.cards;

public interface BoostableCard {
    boolean isBoosted();

    void setBoosted(boolean boosted);

    void notifyBoostEnded(CardArea boostTriggerLocation);

    AllCardCodes getCardCode();

}
