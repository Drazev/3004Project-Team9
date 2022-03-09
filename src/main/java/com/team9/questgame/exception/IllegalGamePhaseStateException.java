package com.team9.questgame.exception;

import com.team9.questgame.Entities.Players;
import com.team9.questgame.GamePhases.GamePhaseControllers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IllegalGamePhaseStateException extends RuntimeException {
    Logger LOG = LoggerFactory.getLogger(IllegalGamePhaseStateException.class);
    static String defaultMsg="Game Phase Encountered an Error. ";
    GamePhaseControllers gamePhase;
    GamePhaseExceptionReasonCodes reasonCode;

    public enum GamePhaseExceptionReasonCodes {
        FAILURE_TO_PAUSE,
        NULL_ACTIVE_PHASE,
        UNEXPECTED_STATE
    }

    public GamePhaseExceptionReasonCodes getReasonCode() {
        return reasonCode;
    }

    public GamePhaseControllers getGamePhaseController() {
        return gamePhase;
    }

    public IllegalGamePhaseStateException(GamePhaseControllers gamePhase, GamePhaseExceptionReasonCodes reasonCode) {
        super(defaultMsg+reasonCode);
        LOG.error(defaultMsg+reasonCode);
        this.gamePhase=gamePhase;
        this.reasonCode=reasonCode;
    }

    public IllegalGamePhaseStateException(Throwable cause, GamePhaseControllers gamePhase, GamePhaseExceptionReasonCodes reasonCode) {
        super(defaultMsg+reasonCode,cause);
        LOG.error(defaultMsg+reasonCode);
        this.gamePhase=gamePhase;
        this.reasonCode=reasonCode;
    }


}
