package com.team9.questgame.exception;

import com.team9.questgame.Entities.Players;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IllegalGameStateException extends RuntimeException{
    Logger LOG = LoggerFactory.getLogger(Players.class);
    static String defaultMsg="The game has reached an invalid state";

    public IllegalGameStateException() {
        super(defaultMsg+".");
        LOG.error(defaultMsg+".");
    }

    public IllegalGameStateException(String message) {
        super(defaultMsg+": "+message);
        LOG.error(defaultMsg+": "+message);
    }

    public IllegalGameStateException(String message, Throwable cause) {
        super(defaultMsg+": "+message,cause);
        LOG.error(defaultMsg+": "+message);
    }

    public IllegalGameStateException(Throwable cause) {
        super(defaultMsg+".",cause);
        LOG.error(defaultMsg+".");
    }
}
