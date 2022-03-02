package com.team9.questgame.exception;

import com.team9.questgame.Entities.Players;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class IllegalCardStateException extends RuntimeException {
    Logger LOG = LoggerFactory.getLogger(Players .class);
    static String defaultMsg="A card has been assigned to an invalid location and was lost.";

    public IllegalCardStateException() {
        super(defaultMsg);
        LOG.error(defaultMsg);
    }

    public IllegalCardStateException(String message) {
        super(message);
        LOG.error(message);
    }

    public IllegalCardStateException(String message, Throwable cause) {
        super(message,cause);
        LOG.error(message);
    }

    public IllegalCardStateException(Throwable cause) {
        super(defaultMsg,cause);
        LOG.error(defaultMsg);
    }

}
