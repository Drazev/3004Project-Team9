package com.team9.questgame.Entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Data.PlayerData;
import com.team9.questgame.Entities.cards.*;
import com.team9.questgame.exception.BadRequestException;
import com.team9.questgame.exception.IllegalCardStateException;
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
    private boolean isHandOversize;

    @JsonIgnore
    private OutboundService outboundService;

    @JsonIgnore
    private static long nextId = 0;
    @Getter
    @Setter
    private boolean isReady;

    @JsonIgnore
    static public final int MAX_HAND_SIZE = 12;

    public Players(String playerName)
    {
        LOG= LoggerFactory.getLogger(Players.class);
        playArea = new PlayerPlayAreas(this);
        hand = new Hand(this,playArea);
        this.name=playerName;
        rank=PlayerRanks.SQUIRE;
        this.playerId=nextId++;
        this.outboundService = ApplicationContextHolder.getContext().getBean(OutboundService.class);
        this.isReady = true;
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
    }

    public int getShields() {
        return shields;
    }

    public PlayerRanks getRank() {
        return rank;
    }

    public HashMap<CardTypes, HashSet<AllCardCodes>> getUniqueCardsCodesBySubType() {
        return hand.getUniqueCardsCodesBySubType();
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

    public void notifyPlayerRankUP() {
        //TODO: Notify game and player of a Rank Up event. Game will check victory condition, and player UI must be updated
        outboundService.broadcastPlayerRankUpdate(generatePlayerData());
    }

    public PlayerData generatePlayerData() {

        PlayerData data = new PlayerData(
        playerId,
        name,
        rank,
        battlePoints,
        shields,
        isHandOversize,
        hand.getCardData()
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
        this.isHandOversize=false;
        this.hand.onGameReset();
        LOG.info(name+": Has reset to GAME START state.");
    }

    public void notifyHandChanged() {
        outboundService.broadcastPlayerHandUpdate(generatePlayerData());
    }

}
