package com.team9.questgame.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IllegalQuestPhaseStateException extends RuntimeException{
    Logger LOG = LoggerFactory.getLogger(IllegalQuestPhaseStateException.class);
    static String defaultMsg = "The Quest has reached an invalid state";

    public IllegalQuestPhaseStateException(String message){
        super(defaultMsg + ": " + message);
        LOG.error(defaultMsg + ": " + message);
    }
}
