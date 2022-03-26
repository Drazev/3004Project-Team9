package com.team9.questgame.Entities.Effects;

import com.team9.questgame.Entities.cards.CardArea;
import com.team9.questgame.Entities.cards.CardWithEffect;
import com.team9.questgame.Entities.cards.Cards;

public interface EffectObserver<T extends Cards> extends CardArea<T> {

    /**
     * Algorithm to handle an effect that has fully resolved.
     * This should discard the card after removing all references to the card within the class.
     * @param resolvedCard The card that was resolved originating from this location.
     */
    void onEffectResolved(CardWithEffect resolvedCard);

    /**
     * Algorithm to handle an effect that has finished being played, but is waiting on a trigger
     * before resolving. The EffectResolverService will own and discard the card.
     * The CardArea implementing this should NOT discard the card, but continue execution.
     * @param resolvedCard The card that was played originating from this location. It is not yet resolved.
     */
    void onEffectResolvedWithDelayedTrigger(CardWithEffect resolvedCard);
}
