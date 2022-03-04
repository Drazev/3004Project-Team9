package com.team9.questgame.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team9.questgame.Data.CardData;
import com.team9.questgame.Data.PlayerData;
import com.team9.questgame.Entities.cards.AdventureCards;
import com.team9.questgame.Entities.cards.CardArea;
import com.team9.questgame.Entities.cards.Cards;
import com.team9.questgame.exception.IllegalCardStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
public class Players implements CardArea<AdventureCards> {
    @JsonIgnore
    private Logger LOG;
    private final long playerId; //Unique to player game session. Does not persist between games.
    private String name;
    private PlayerRanks rank;
    private int battlePoints;
    private int shields;
    private boolean isHandOversize;

    @JsonIgnore
    private static long nextId=0;

    @JsonIgnore
    static public final int MAX_HAND_SIZE = 12;
    private HashSet<AdventureCards> hand;

    @JsonIgnore
    private HashMap<Long,AdventureCards> cardIdMap;

    public Players(String playerName)
    {
        LOG= LoggerFactory.getLogger(Players.class);
        this.name=playerName;
        rank=PlayerRanks.SQUIRE;
        this.playerId=nextId++;
        onGameReset();
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

    public boolean isHandOversize() {
        return isHandOversize;
    }

    public int getHandSize() { return hand.size(); }

    public int getBattlePoints() {
        return battlePoints;
    }

    public PlayerRanks getRank() {
        return rank;
    }

    private void updateBattlePoints()
    {
        battlePoints=rank.getRankBattlePointValue(); //TODO: Modify to include bp value of cards in play
    }

    private boolean validateHandSize() {
        boolean prevState=isHandOversize;
        isHandOversize = hand.size()>MAX_HAND_SIZE;
        if(isHandOversize) {
            LOG.debug(name+": Oversize Hand State SET.");
            notifyHandOversize();
        }
        else if(prevState) {
            LOG.debug(name+": Oversize Hand State CLEARED.");
        }
        return isHandOversize;
    }

    @Override
    public void receiveCard(AdventureCards card) {
        hand.add(card);
        cardIdMap.put(card.getCardID(),card);
        LOG.info(name+": Has DRAWN a card.");
        validateHandSize();
        notifyHandChanged();
    }

    @Override
    public void discardCard(AdventureCards card) {
        if(hand.contains(card)==false)
        {
            //TODO: Handle card was not in hand
        }

        card.discardCard();
        hand.remove(card);
        LOG.info(name+": Has DISCARDED a card.");
        validateHandSize();
    }

    public void discardCard(long cardId ) {
        AdventureCards card = findCardFromCardId(cardId);
        discardCard(card);
    }


    @Override
    public void playCard(AdventureCards card) {
        LOG.info(name+": Has PLAYED CARD "+card);
        notifyHandChanged();
    }

    public void playCard(long cardId) {
        AdventureCards card = findCardFromCardId(cardId);
        playCard(card);
    }

    private AdventureCards findCardFromCardId(Long cardId) {
        AdventureCards card = cardIdMap.get(cardId);
        if(card==null) {

            //If we get null, determine if it was bad request or internal error
            if(cardIdMap.containsKey(cardId))
            {
                //The map did not contain the cardId, BAD REQUEST
                //TODO: Trigger BAD_REQUEST to front end
            }
            else {
                //The key was mapped to a null card value. This should not happen and is an illegal state
                LOG.error("Player hand state has lost a card in cardIdMap");
                throw new IllegalCardStateException();
            }
        }
        return card;
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
            notifyPlayerRankUP();
        }

    }

    public PlayerData generatePlayerData() {
        ArrayList<CardData> handCards = new ArrayList<>();
        for(Cards card : hand) {
            handCards.add(card.generateCardData());
        }
        PlayerData data = new PlayerData(
        playerId,
        name,
        rank,
        battlePoints,
        shields,
        isHandOversize,
        handCards
        );
        return data;
    }

    /**
     * Play area clear's all cards other than allies from their play area.
     *
     */
    public void onEndOfPhase() {

    }

    /**
     * Resets Player data and state to start of game condition
     */
    @Override
    public void onGameReset() {
        this.rank=PlayerRanks.SQUIRE;
        this.battlePoints=rank.getRankBattlePointValue();
        this.shields=0;
        this.isHandOversize=false;
        this.hand = new HashSet<>();
        this.cardIdMap = new HashMap<>();
        notifyHandChanged();
        LOG.info(name+": Has reset to GAME START state.");
    }

    private void notifyHandOversize() {

    }

    public void notifyHandChanged() {

    }

    public void notifyPlayerRankUP() {
        //TODO: Notify game and player of a Rank Up event. Game will check victory condition, and player UI must be updated
    }

}
