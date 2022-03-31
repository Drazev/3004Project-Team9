package com.team9.questgame.Entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Data.PlayerData;
import com.team9.questgame.Entities.cards.*;
import com.team9.questgame.exception.BadRequestException;
import com.team9.questgame.exception.CardAreaException;
import com.team9.questgame.exception.IllegalCardStateException;
import com.team9.questgame.gamemanager.record.socket.NotificationOutbound;
import com.team9.questgame.gamemanager.service.InboundService;
import com.team9.questgame.gamemanager.service.NotificationOutboundService;
import com.team9.questgame.gamemanager.service.OutboundService;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Entity representation of a Player registered to an individual game session.
 * This class governs the individual actions a player can choose or be forced to take.
 * Actions that are player driven are prefixed with 'action' while events driven by the game
 * system are prefixed with 'on'
 *
 * This class emits events when a player discards a card or is has an oversize hand.
 *
 * This class owns a PlayerArea and contains a hand;
 */
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property="playerId"
)
public class Players {
    @JsonIgnore
    private Logger LOG;
    private final long playerId; //Unique to player game session. Does not persist between games.
    @JsonIgnore
    private final PlayerPlayAreas playArea;
    private String name;
    private PlayerRanks rank;
    private int shields;
    private Hand hand;

    @JsonIgnore
    private static long nextId = 0;
    @Getter
    @Setter
    private boolean isReady;

    public Players(String playerName)
    {
        this.name=playerName;
        LOG= LoggerFactory.getLogger(Players.class);
        playArea = new PlayerPlayAreas(this);
        rank=PlayerRanks.SQUIRE;
        this.playerId=nextId++;
        this.isReady = true;
        hand = new Hand(this,playArea);
    }

    public PlayerPlayAreas getPlayArea() {
        return playArea;
    }

    public Hand getHand() {
        return hand;
    }

    public long getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPlayerDataChanged();
    }

    public int getShields() {
        return shields;
    }

    public PlayerRanks getRank() {
        return rank;
    }

    public boolean awardShields(int shieldsRewarded) {
        if(shieldsRewarded>0) {
            shields+=shieldsRewarded;
            updateRank();
            notifyPlayerDataChanged();
            return true;
        }
        return false;
    }

    public boolean looseShields(int shieldsLost) {
        if(shields<1) {
            return false;
        }
        else if(shields<shieldsLost) {
            shields = 0;
        }
        else {
            shields-=shieldsLost;
        }
        notifyPlayerDataChanged();
        return true;
    }

    public void actionPlayCard(long cardId) throws BadRequestException,IllegalCardStateException, CardAreaException {
        hand.playCard(cardId);
    }

    public void actionDiscardCard(long cardId) throws BadRequestException,IllegalCardStateException {
        hand.discardCard(cardId);
    }

    public void actionActivateCard(long cardId) throws BadRequestException,IllegalCardStateException {

    }

    /**
     * Checks shields player owns and ranks up
     * the player if they have the sufficent
     * amount of shields.
     *
     * Triggers update event when rank is changed
     */
    private void updateRank() {
        boolean rankUp=false;

        while(shields>=rank.getNextRankCost()) {
            rankUp=true;
            shields-=rank.getNextRankCost();
            rank=rank.getNextRank();
            LOG.info("Player "+name+" has attained rank "+rank);

        }

        if(rankUp) {
            playArea.update();
            notifyPlayerDataChanged();
        }
    }

    private void notifyPlayerDataChanged() {
        NotificationOutbound msg = new NotificationOutbound("Player Promoted To "+rank,String.format("You have spent %d shields and been promoted the the rank %s. You are now one step closer to becoming the newest Knight of the Round Table!",rank.getRankShieldCost(),rank),rank.getImgSrc(),null);
        NotificationOutbound msgToOthers = new NotificationOutbound("Player Promoted",String.format("The player %s was promoted to the rank %s",name,rank),rank.getImgSrc(),null);
        NotificationOutboundService.getService().sendGoodNotification(this,msg,null);
        NotificationOutboundService.getService().sendInfoNotification(this,null,msgToOthers);

        OutboundService.getService().broadcastPlayerDataChanged(this,generatePlayerData());
    }

    public PlayerData generatePlayerData() {

        PlayerData data = new PlayerData(
        playerId,
        hand.getHandId(),
        playArea.getPlayAreaId(),
        name,
        rank,
        rank.getRankBattlePointValue(),
        shields
        );
        return data;
    }

    /**
     * Resets Player data and state to start of game condition
     */
    public void onGameReset() {
        this.rank=PlayerRanks.SQUIRE;
        this.shields=0;
        this.hand.onGameReset();
        this.playArea.onGameReset();
        LOG.info(name+": Has reset to GAME START state.");
        notifyPlayerDataChanged();
    }

}
