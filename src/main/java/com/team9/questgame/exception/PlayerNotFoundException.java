package com.team9.questgame.exception;

import com.team9.questgame.Entities.Players;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerNotFoundException extends RuntimeException {
    Logger LOG = LoggerFactory.getLogger(PlayerNotFoundException.class);
    static String defaultMsg="Player was not found in game.";
    Players player;

    public PlayerNotFoundException(Players player) {
        super(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}");
        LOG.error(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}");
        this.player=player;
    }

    public PlayerNotFoundException(String message,Players player) {
        super(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}, Reason: "+message);
        LOG.error(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}, Reason: "+message);
        this.player=player;
    }

    public PlayerNotFoundException(String message, Throwable cause,Players player) {
        super(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}, Reason: "+message,cause);
        LOG.error(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}, Reason: "+message);
        this.player=player;
    }

    public PlayerNotFoundException(Throwable cause,Players player) {
        super(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}",cause);
        this.player=player;
    }

    public PlayerNotFoundException(Throwable cause) {
        super(defaultMsg+".",cause);
        LOG.error(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}");
        this.player=player;
    }

    Players getPlayer() {
        return player;
    }
}
