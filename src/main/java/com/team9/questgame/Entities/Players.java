package com.team9.questgame.Entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Data.PlayerData;
import com.team9.questgame.Entities.cards.*;
import com.team9.questgame.exception.BadRequestException;
import com.team9.questgame.exception.IllegalCardStateException;
import com.team9.questgame.gamemanager.service.InboundService;
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
    private int battlePoints;
    private int shields;
    private Hand hand;

    @JsonIgnore
    private OutboundService outboundService;
    @JsonIgnore
    private final InboundService inboundService;

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
        this.outboundService = ApplicationContextHolder.getContext().getBean(OutboundService.class);
        this.inboundService = ApplicationContextHolder.getContext().getBean(InboundService.class);
        this.isReady = true;
        hand = new Hand(this,playArea);
        onGameReset();
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
        return true;
    }

    public void actionPlayCard(long cardId) throws BadRequestException,IllegalCardStateException {
        hand.playCard(cardId);
    }

    public void actionDiscardCard(long cardId) throws BadRequestException,IllegalCardStateException {
        hand.discardCard(cardId);
    }

    public void actionActivateCard(long cardId) throws BadRequestException,IllegalCardStateException {
        playArea.activateCard(cardId);
    }

    /**
     * Checks shields player owns and ranks up
     * the player if they have the sufficent
     * amount of shields.
     *
     * Triggers update event when rank is changed
     */
    private void updateRank() {
        PlayerRanks nextRank;
        switch(rank) {
            case SQUIRE:
                nextRank=PlayerRanks.KNIGHT;
                break;
            case KNIGHT:
                nextRank=PlayerRanks.CHAMPION_KNIGHT;
                break;
            case CHAMPION_KNIGHT:
                nextRank=PlayerRanks.KNIGHT_OF_ROUND_TABLE;
            default:
                LOG.debug("Player rank checked after victory condition set");
                nextRank=PlayerRanks.KNIGHT_OF_ROUND_TABLE;
        }

        if(shields>=nextRank.getRankShieldCost()) {
            shields-=nextRank.getRankShieldCost();
            rank=nextRank;
            LOG.info("Player "+name+" has attained rank "+rank);
            playArea.update();
            notifyPlayerRankUP();
        }

    }

    private void notifyPlayerRankUP() {
        //TODO: Notify game and player of a Rank Up event. Game will check victory condition, and player UI must be updated
        inboundService.playerNotifyPlayerRankUP(this,rank);
    }

    private void notifyPlayerDataChanged() {
        outboundService.broadcastPlayerDataChanged(this,generatePlayerData());
    }

    public PlayerData generatePlayerData() {

        PlayerData data = new PlayerData(
        playerId,
        name,
        rank,
        battlePoints,
        shields
        );
        return data;
    }

    /**
     * Resets Player data and state to start of game condition
     */
    public void onGameReset() {
        this.rank=PlayerRanks.SQUIRE;
        this.battlePoints=rank.getRankBattlePointValue();
        this.shields=0;
        this.hand.onGameReset();
        LOG.info(name+": Has reset to GAME START state.");
        notifyPlayerDataChanged();
    }

}
