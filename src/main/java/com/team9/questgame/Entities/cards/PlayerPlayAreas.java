package com.team9.questgame.Entities.cards;

import com.fasterxml.jackson.annotation.*;
import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Data.CardData;
import com.team9.questgame.Data.PlayAreaData;
import com.team9.questgame.Data.PlayAreaDataSources;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.GamePhases.GamePhaseControllers;
import com.team9.questgame.exception.CardAreaException;
import com.team9.questgame.exception.IllegalGamePhaseStateException;
import com.team9.questgame.gamemanager.service.InboundService;
import com.team9.questgame.gamemanager.service.OutboundService;

import java.util.*;

import static com.team9.questgame.exception.IllegalGamePhaseStateException.GamePhaseExceptionReasonCodes.NULL_ACTIVE_PHASE;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property="id"
)
public class PlayerPlayAreas implements PlayAreas<AdventureCards> {

    private long id;
    @JsonIgnore
    private final Players player;
    @JsonIgnore
    private GamePhaseControllers phaseController;
    @JsonIgnore
    private final OutboundService outboundService;
    private PlayAreas<AdventureCards> targetPlayArea;
    private int bids;
    private int battlePoints;
    private HashMap<CardTypes, HashSet<AdventureCards>> cardTypeMap;
    private HashMap<AllCardCodes,AdventureCards> allCards;
    private QuestCards questCard;

    //Managed by Registration methods and triggered by cards
    private HashMap<AllCardCodes,HashSet<BoostableCard>> cardBoostDependencies;
    private HashMap<AllCardCodes,BoostableCard> allBoostableCards;
    private HashSet<AdventureCards> cardsWithActiveEffects;
    private HashSet<BattlePointContributor> cardsWithBattleValue;
    private HashSet<BidContributor> cardsWithBidValue;

    static private long nextid=0;

    public PlayerPlayAreas(Players player) {
        this.id = nextid++;
        this.player = player;
        cardTypeMap = new HashMap<>();
        allCards = new HashMap<>();
        cardBoostDependencies = new HashMap<>();
        cardsWithActiveEffects = new HashSet<>();
        phaseController = null;
        cardsWithBattleValue = new HashSet<>();
        cardsWithBidValue = new HashSet<>();
        allBoostableCards = new HashMap<>();
        targetPlayArea=null;
        bids=0;
        battlePoints=0;
        questCard=null;
        this.outboundService = ApplicationContextHolder.getContext().getBean(OutboundService.class);
    }

    @Override
    public int getBattlePoints() {
        return battlePoints;
    }

    @Override
    public int getBids() {
        return bids;
    }

    public int size() {
        return allCards.size();
    }

    public ArrayList<CardData> getCardData() {
        ArrayList<CardData> handCards = new ArrayList<>();
        for(Cards card : allCards.values()) {
            handCards.add(card.generateCardData());
        }
        return handCards;
    }

    public PlayAreaData getPlayAreaData() {
        HashSet<CardTypes> allowedTypes = new HashSet<>();
        allowedTypes.add(CardTypes.ALLY);
        allowedTypes.add(CardTypes.WEAPON);
        allowedTypes.add(CardTypes.AMOUR);
        PlayAreaData data = new PlayAreaData(
                PlayAreaDataSources.PLAYER,
                id,
                bids,
                battlePoints,
                allowedTypes,
                getCardData()
        );
        return data;
    }

    void discardAllCards() {
        for(AdventureCards card : allCards.values()) {
            card.discardCard();
        }
        cardTypeMap.clear();
        allCards.clear();
        update();
    }

    public HashMap<CardTypes, HashSet<AdventureCards>> getCardTypeMap() {
        return cardTypeMap;
    }

    public void discardAllFoes() {
        HashSet<AdventureCards> cardList = cardTypeMap.get(CardTypes.FOE);
        discardCards(cardList);
    }

    public void discardAllAllies() {
        HashSet<AdventureCards> cardList = cardTypeMap.get(CardTypes.ALLY);
        discardCards(cardList);
    }

    public void discardAllWeapons() {
        HashSet<AdventureCards> cardList = cardTypeMap.get(CardTypes.WEAPON);
        discardCards(cardList);
    }

    public void discardAllTests() {
        HashSet<AdventureCards> cardList = cardTypeMap.get(CardTypes.TEST);
        discardCards(cardList);
    }

    public void discardAllAmour() {
        HashSet<AdventureCards> cardList = cardTypeMap.get(CardTypes.AMOUR);
        discardCards(cardList);
    }

    @Override
    public void discardCard(AdventureCards card) {
        card.discardCard(); //This will trigger all affected cards boost status to reset
        removeCard(card);
        update();
    }

    /**
     * Add a card to the Players In play area.
     * Cards are recieved by Players Hand when they
     * attempt to play a card from their hand.
     * @param card
     */
    @Override
    public boolean receiveCard(AdventureCards card) throws CardAreaException {
        boolean rc = false;
        if(card==null) {
            throw new CardAreaException(CardAreaException.CardAreaExceptionReasonCodes.NULL_CARD);
        }

        AllCardCodes<AdventureDeckCards> cardCode = card.getCardCode();


        if (allCards.containsValue(card)) {
            throw new CardAreaException(CardAreaException.CardAreaExceptionReasonCodes.DUPLICATE_CARD_INSTANCE);
        }

        switch(card.getSubType()) {
            case FOE:
            case TEST:
                rc=playCard(card);
                break;
            default:
                addToPlayArea(card);
                rc=true;
                break;
        }

        return rc; //Player Play area's does not reject cards
    }



    @Override
    public boolean playCard(AdventureCards card) {
        if(targetPlayArea==null) {
            return false;
        }
        boolean rc = card.playCard(targetPlayArea);

        if(rc) {
            rc=removeCard(card);
            update();
        }
        return rc;
    }

    /**
     * Cards may register a boost trigger. This trigger is checked with a card is added or removed from
     * the play area.
     * @param triggerCardCode A card code that will trigger a boost for the card
     * @param card The card that would be boosted if trigger is found
     * @return
     */
    public void registerCardBoostDependency(AllCardCodes triggerCardCode, BoostableCard card) {
        HashSet<BoostableCard> list = cardBoostDependencies.get(triggerCardCode);

        if(list==null)
        {
            list = new HashSet<>();
            cardBoostDependencies.put(triggerCardCode,list);
        }

        //If card is already in play area, trigger the boost.
        list.add(card);
        AdventureCards triggerCard = allCards.get(triggerCardCode);
        if(triggerCard!=null)
        {
            triggerCard.registerBoostedCard(card);
            card.setBoosted(true);
            update();
        }
    }

    public void registerBoostableCard(AllCardCodes cardCode,BoostableCard card) {
        allBoostableCards.put(cardCode,card);
        update();
    }

    public void registerBidContributor(BidContributor card) {
        cardsWithBidValue.add(card);
        updateBids();
        notifyPlayAreaChanged();
    }

    public void registerBattlePointContributor(BattlePointContributor card) {
        cardsWithBattleValue.add(card);
        updateBattlePoints();
        notifyPlayAreaChanged();
    }

    public boolean registerActiveEffect(AdventureCards card) {
        return cardsWithActiveEffects.add(card);
    }

    public void registerGamePhase(GamePhaseControllers activePhase) {
        if(activePhase==null)
        {
            throw new IllegalGamePhaseStateException(null,NULL_ACTIVE_PHASE);
        }
        this.phaseController=activePhase;
    }

    public boolean activateCard(long cardId) {
        return true; //TODO: Implement with active effects
    }

    @Override
    public void onGamePhaseEnded() {
        //Unset Quest boost if quest card was set
        if(questCard!=null && allBoostableCards.containsKey(questCard.getBoostedFoe())) {
            allBoostableCards.get(questCard.getBoostedFoe()).setBoosted(false);
        }
        phaseController=null;
        targetPlayArea=null;
        questCard=null;
        discardAllAmour();
        discardAllFoes();
        discardAllWeapons();
        update();
    }

    @Override
    public void onGameReset() {
        cardTypeMap.clear();
        allCards.clear();
        cardBoostDependencies.clear();
        cardsWithActiveEffects.clear();
        phaseController = null;
        cardsWithBattleValue.clear();
        cardsWithBidValue.clear();
        allBoostableCards.clear();
        bids=0;
        battlePoints=0;
        questCard=null;
        targetPlayArea=null;
    }

    public void onPlayAreaChanged(PlayAreas targetPlayArea) {
        if(phaseController==null) {
            return;
        }
        this.targetPlayArea=targetPlayArea;
    }

    public void onQuestStarted(QuestCards questCard) {
        this.questCard=questCard;
        if(allBoostableCards.containsKey(questCard.getBoostedFoe())) {
            allBoostableCards.get(questCard.getBoostedFoe()).setBoosted(true);
        }
    }

    private boolean removeCard(AdventureCards card) {
        if(card==null) {
            return false;
        }

        AdventureCards delCard = allCards.get(card.cardCode);

        if(delCard!=card) {
            return false;
        }

        AllCardCodes cardCode = card.getCardCode();
        allCards.remove(cardCode);
        cardTypeMap.get(cardCode.getSubType()).remove(card);

        cardBoostDependencies.remove(cardCode);
        allBoostableCards.remove(cardCode);
        cardsWithActiveEffects.remove(delCard);
        cardsWithBattleValue.remove(delCard);
        cardsWithBidValue.remove(delCard);
        return true;
    }

    public void update() {
        updateBids();
        updateBattlePoints();
        notifyPlayAreaChanged();
    }

    private void updateBids() {
        int newBids=0;

        for(BidContributor card : cardsWithBidValue) {
            newBids+=card.getBids();
        }

        bids=newBids;
    }

    private void updateBattlePoints() {
        int newBattlePoints=player.getRank().getRankBattlePointValue();

        for(BattlePointContributor card : cardsWithBattleValue) {
            newBattlePoints+=card.getBattlePoints();
        }
        battlePoints=newBattlePoints;
    }

    /**
     * Discards all cards from play area in list.
     *
     * @sideEffects Card discard function will trigger all observing boosted cards to clear boost status.
     * @param cardList The list of cards to be discarded
     */
    private void discardCards(HashSet<AdventureCards> cardList)
    {
        HashSet<AdventureCards> list = new HashSet<>(cardList);
        for(AdventureCards card : list) {
            discardCard(card);
        }
        cardList.clear();
        update();
    }

    private void addToPlayArea(AdventureCards card) throws CardAreaException {
        //GameRule:: Cannot play two of the same card
        if(allCards.containsKey(card.getCardCode())) {
            throw new CardAreaException(CardAreaException.CardAreaExceptionReasonCodes.RULE_CANNOT_HAVE_TWO_OF_SAME_CARD_IN_PLAY);
        }

        allCards.put(card.getCardCode(),card);

        CardTypes cardType = card.getCardCode().getSubType();
        if(!cardTypeMap.containsKey(cardType))
        {
            cardTypeMap.put(cardType,new HashSet<>());
        }

        cardTypeMap.get(cardType).add(card);

        //Check to see if the new card is listed as a boost dependency list and boost all associated cards if found
        if(cardBoostDependencies.containsKey(card.getCardCode())) {
            for(BoostableCard boostCard : cardBoostDependencies.get(card.getCardCode())) {
                boostCard.setBoosted(true);
                card.registerBoostedCard(boostCard);
            }
        }

        if(questCard!=null && allBoostableCards.containsKey(questCard.getBoostedFoe())) {
            allBoostableCards.get(questCard.getBoostedFoe()).setBoosted(true);
        }

        update();
    }

    private void notifyPlayAreaChanged() {
        outboundService.broadcastPlayAreaChanged(player,getPlayAreaData());
    }
}
