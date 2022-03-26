package com.team9.questgame.Entities.Effects;
import com.team9.questgame.Entities.cards.Cards;

public interface DiscardSubject {
    void registerDiscardObserver(DiscardObserver observer);
    void unregisterDiscardObserver(DiscardObserver observer);
    void notifyDiscardObservers(Cards card);
}
