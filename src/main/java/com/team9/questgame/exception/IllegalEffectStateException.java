package com.team9.questgame.exception;

import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.cards.Cards;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IllegalEffectStateException extends RuntimeException {
    Logger LOG = LoggerFactory.getLogger(IllegalEffectStateException.class);
    static String defaultMsg="An effect was activated but reached an unexpected state.";
    final private Effects effect;

    public Effects getEffect() {
        return effect;
    }

    public Cards getCard() {
        return card;
    }

    final private Cards card;

    public IllegalEffectStateException(Effects effect, Cards card) {
        super(defaultMsg+" CARD: "+card.getCardName());
        this.effect = effect;
        this.card = card;
        LOG.error(defaultMsg);
    }

    public IllegalEffectStateException(String message, Effects effect, Cards card) {
        super(defaultMsg+" CARD: "+card.getCardName()+", REASON: "+message);
        this.effect = effect;
        this.card = card;
        LOG.error(message);
    }

    public IllegalEffectStateException(String message, Throwable cause, Effects effect, Cards card) {
        super(defaultMsg+" CARD: "+card.getCardName()+", REASON: "+message,cause);
        this.effect = effect;
        this.card = card;
        LOG.error(message);
    }

    public IllegalEffectStateException(Throwable cause, Effects effect, Cards card) {
        super(defaultMsg+" CARD: "+card.getCardName(),cause);
        this.effect = effect;
        this.card = card;
        LOG.error(defaultMsg);
    }

}
