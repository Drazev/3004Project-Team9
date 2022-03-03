package com.team9.questgame.exception;

import com.team9.questgame.Entities.Players;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerJoinException extends RuntimeException {
    Logger LOG = LoggerFactory.getLogger(Players.class);
    static String defaultMsg="Player failed to join game.";
    Players player;

    public PlayerJoinException(String message,Players player) {
        super(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}, Reason: "+message);
        LOG.error(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}, Reason: "+message);
        this.player=player;
    }

    public PlayerJoinException(String message, Throwable cause,Players player) {
        super(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}, Reason: "+message,cause);
        LOG.error(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}, Reason: "+message);
        this.player=player;
    }

    Players getPlayer() {
        return player;
    }
}
