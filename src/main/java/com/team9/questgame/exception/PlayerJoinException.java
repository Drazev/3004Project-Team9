package com.team9.questgame.exception;

import com.team9.questgame.Entities.Players;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerJoinException extends RuntimeException {
    Logger LOG = LoggerFactory.getLogger(Players.class);
    static String defaultMsg="Player failed to join game.";
    Players player;
    PlayerJoinExceptionReasonCodes reasonCode;

    public enum PlayerJoinExceptionReasonCodes {
        GAME_FULL,
        GAME_IN_PROGRESS
    }

    public PlayerJoinException(Players player,PlayerJoinExceptionReasonCodes reasonCode) {
        super(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}, Reason: "+reasonCode);
        LOG.error(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}, Reason: "+reasonCode);
        this.player=player;
        this.reasonCode=reasonCode;
    }

    public PlayerJoinException(Throwable cause,Players player,PlayerJoinExceptionReasonCodes reasonCode) {
        super(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}, Reason: "+reasonCode,cause);
        LOG.error(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}, Reason: "+reasonCode);
        this.player=player;
        this.reasonCode=reasonCode;
    }

    Players getPlayer() {
        return player;
    }
}
