package com.team9.questgame.exception;

import com.team9.questgame.Entities.Players;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IllegalGameRequest extends RuntimeException{
    Logger LOG = LoggerFactory.getLogger(IllegalGameRequest.class);
    static String defaultMsg="Player made invalid request from the game.";
    Players player;

    public IllegalGameRequest(Players player) {
        super(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}");
        LOG.error(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}");
        this.player=player;
    }

    public IllegalGameRequest(String message,Players player) {
        super(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}, Reason: "+message);
        LOG.error(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}, Reason: "+message);
        this.player=player;
    }

    public IllegalGameRequest(String message, Throwable cause,Players player) {
        super(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}, Reason: "+message,cause);
        LOG.error(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}, Reason: "+message);
        this.player=player;
    }

    public IllegalGameRequest(Throwable cause,Players player) {
        super(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}",cause);
        this.player=player;
    }

    public IllegalGameRequest(Throwable cause) {
        super(defaultMsg+".",cause);
        LOG.error(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}");
        this.player=player;
    }


    Players getPlayer() {
        return player;
    }
}
