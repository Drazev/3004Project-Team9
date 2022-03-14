package com.team9.questgame.exception;

import com.team9.questgame.game_phases.GamePhases;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IllegalGamePhaseStateException extends RuntimeException {
    Logger LOG = LoggerFactory.getLogger(IllegalGamePhaseStateException.class);
    static String defaultMsg="Game Phase Encountered an Error. ";
    GamePhases gamePhase;
    GamePhaseExceptionReasonCodes reasonCode;

    public enum GamePhaseExceptionReasonCodes {
        FAILURE_TO_PAUSE,
        NULL_ACTIVE_PHASE,
        UNEXPECTED_STATE
    }

    public GamePhaseExceptionReasonCodes getReasonCode() {
        return reasonCode;
    }

    public GamePhases getGamePhaseController() {
        return gamePhase;
    }

    public IllegalGamePhaseStateException(GamePhases gamePhase, GamePhaseExceptionReasonCodes reasonCode) {
        super(defaultMsg+reasonCode);
        LOG.error(defaultMsg+reasonCode);
        this.gamePhase=gamePhase;
        this.reasonCode=reasonCode;
    }

    public IllegalGamePhaseStateException(Throwable cause, GamePhases gamePhase, GamePhaseExceptionReasonCodes reasonCode) {
        super(defaultMsg+reasonCode,cause);
        LOG.error(defaultMsg+reasonCode);
        this.gamePhase=gamePhase;
        this.reasonCode=reasonCode;
    }


}
