package com.team9.questgame.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CardAreaException extends RuntimeException{
    Logger LOG = LoggerFactory.getLogger(CardAreaException.class);
    static String defaultMsg="A PlayArea has entered an unexpected state.";
    CardAreaExceptionReasonCodes reason;

    public enum CardAreaExceptionReasonCodes {
        UNEXPECTED_STATE,
        GAMEPHASE_NOT_REGISTERED,
        STAGE_PLAY_AREA_NOT_SET,
        NULL_CARD,
        DUPLICATE_CARD_INSTANCE,
        RULE_CANNOT_HAVE_TWO_OF_SAME_CARD_IN_PLAY,
        RULE_VIOLATION_CANNOT_PLAY_OR_DISCARD_OUT_OF_TURN
    }

    public CardAreaException(CardAreaExceptionReasonCodes reasonCode) {
        super(defaultMsg+reasonCode);
        LOG.error(defaultMsg+reasonCode);
        reason = reasonCode;
    }

    public CardAreaException(String message, CardAreaExceptionReasonCodes reasonCode) {
        super(message+reasonCode);
        LOG.error(message+reasonCode);
        reason = reasonCode;
    }

    public CardAreaException(String message, Throwable cause, CardAreaExceptionReasonCodes reasonCode) {
        super(message+reasonCode,cause);
        LOG.error(message+reasonCode);
        reason = reasonCode;
    }

    public CardAreaException(Throwable cause, CardAreaExceptionReasonCodes reasonCode) {
        super(defaultMsg+reasonCode,cause);
        LOG.error(defaultMsg+reasonCode);
        reason = reasonCode;
    }
}
