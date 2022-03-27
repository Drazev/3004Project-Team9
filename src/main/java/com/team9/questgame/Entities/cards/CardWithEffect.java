package com.team9.questgame.Entities.cards;

import com.team9.questgame.Entities.Effects.EffectObserver;
import com.team9.questgame.Entities.Players;

public interface CardWithEffect<T extends Cards> {
    String getCardName();
    CardTypes getSubType();
    long getCardID();
    T getCard();
    AllCardCodes getCardCode();
    CardArea getLocation();
    void discardCard();
    void activate(EffectObserver observer, Players activatingPlayer);
}
