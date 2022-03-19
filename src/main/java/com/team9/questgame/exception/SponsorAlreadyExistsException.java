package com.team9.questgame.exception;

import com.team9.questgame.Entities.Players;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SponsorAlreadyExistsException extends RuntimeException{
    Logger LOG = LoggerFactory.getLogger(PlayerNotFoundException.class);
    static String defaultMsg="A Sponsor Already Exists.";
    Players player;

    public SponsorAlreadyExistsException(Players player){
        super(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"} Cannot be made Sponsor");
        LOG.error(defaultMsg+" Player{ name: "+player.getPlayerId()+", id: "+player.getPlayerId()+"}  Cannot be made Sponsor");
        this.player=player;
    }
}
