package com.team9.questgame.Entities.cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Data.CardData;
import com.team9.questgame.Data.StageAreaData;
import com.team9.questgame.Entities.Effects.EffectObserver;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.exception.BadRequestException;
import com.team9.questgame.exception.CardAreaException;
import com.team9.questgame.exception.IllegalCardStateException;
import com.team9.questgame.game_phases.quest.QuestPhaseController;
import com.team9.questgame.gamemanager.service.QuestPhaseOutboundService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class StagePlayAreas implements PlayAreas<AdventureCards>, EffectObserver<AdventureCards> {

    private Players sponsor;
    @Getter
    private int stageNum;
    @Getter
    private long stageID;
    private int battlePoints;
    private int bids;
    @JsonIgnore
    private Logger LOG;
    @Getter
    private HashMap<AllCardCodes, AdventureCards> allCards;
    private QuestCards questCard;

    private QuestPhaseController phaseController;

    private HashMap<AllCardCodes,HashSet<BoostableCard>> cardBoostDependencies;
    private HashSet<BattlePointContributor> cardsWithBattleValue;
    private HashSet<BoostableCard> boostableCards;
    private HashMap<Long, AdventureCards> cardIdMap;
    private boolean hasFoe;
    @Getter
    private boolean hasTest;
    @Getter
    private AdventureCards stageCard;


    private StagePlayAreas targetPlayArea;




    static private int nextid = 0;

    public StagePlayAreas(QuestPhaseController phaseController,QuestCards questCard, Players sponsor, int stageNum){
        this.stageID = nextid++;
        this.questCard = questCard;
        this.sponsor = sponsor;
        this.stageNum = stageNum;
        this.stageCard = null;
        this.phaseController = phaseController;
        allCards = new HashMap<>();
        battlePoints = 0;
        bids = 0;
        cardBoostDependencies = new HashMap<>();
        boostableCards = new HashSet<>();
        cardIdMap = new HashMap<>();
        hasFoe = false;
        hasTest = false;

        cardsWithBattleValue = new HashSet<>();

        this.targetPlayArea=null;

        LOG = LoggerFactory.getLogger(StagePlayAreas.class);
    }

    @Override
    public boolean receiveCard(AdventureCards card){
        if(card==null) {
            throw new CardAreaException(CardAreaException.CardAreaExceptionReasonCodes.NULL_CARD);
        }
        return addToPlayArea(card);
    }

    public void receiveCard(QuestCards questCard){
        this.questCard = questCard;
    }



    private boolean addToPlayArea(AdventureCards card){
        if(allCards.containsKey(card.getCardCode())){
            LOG.error("RULE: A stage cannot have two cards of the same type");
            throw new CardAreaException(CardAreaException.CardAreaExceptionReasonCodes.RULE_CANNOT_HAVE_TWO_OF_SAME_CARD_IN_PLAY);
        }if(hasFoe && card.getSubType() == CardTypes.FOE){
            LOG.error("RULE: A stage cannot have two cards of the same type");
            throw new CardAreaException(CardAreaException.CardAreaExceptionReasonCodes.RULE_STAGE_CANNOT_HAVE_MORE_THAN_ONE_FOE);
        }if(/*card.getSubType()==CardTypes.TEST &&(!(allCards.isEmpty()) || */hasTest){
            LOG.error("RULE: A test card must be the only card in the stage");
            throw new CardAreaException(CardAreaException.CardAreaExceptionReasonCodes.RULE_TEST_MUST_BE_ONLY_CARD_IN_STAGE);
        }
        allCards.put(card.getCardCode(),  card);
        cardIdMap.put(card.getCardID(), card);

        if(card.getSubType() == CardTypes.FOE){
            hasFoe = true;
            stageCard = card;
        }else if(card.getSubType() == CardTypes.TEST){
            hasTest = true;
            stageCard = card;
        }
        updateBattlePoints();

        return true;
    }

    @Override
    public boolean playCard(AdventureCards card){
        if(targetPlayArea==null) {
            return false;
        }
        boolean rc = card.playCard(targetPlayArea);

        if(rc) {
            rc=removeCard(card);
            updateBattlePoints();
        }
        return rc;
    }

    public boolean playCard(long cardID, PlayAreas targetPlayArea){
        AdventureCards card = findCardFromCardId(cardID);
        if(targetPlayArea == null){
            return false;
        }
        boolean rc = card.playCard(targetPlayArea);

        if(rc){
            rc=removeCard(card);
            updateBattlePoints();
        }
        return rc;

    }
    public boolean returnToHand(long cardID){
        AdventureCards card = findCardFromCardId(cardID);
        boolean rc = card.playCard(sponsor.getHand());
        if(card == null){
            rc = false;
        }

        if(rc){
            rc = removeCard(card);
            updateBattlePoints();
        }
        return rc;

    }

    /**
     * Helper method to find the associated adventure card to the cardId within the stage
     * @param cardId The cardID we want to find
     * @return found adventure card
     */
    private AdventureCards findCardFromCardId(Long cardId) throws BadRequestException, IllegalCardStateException {
        AdventureCards card = cardIdMap.get(cardId);
        if(card==null) {
            //If we get null, determine if it was bad request or internal error
            if(!cardIdMap.containsKey(cardId))
            {
                //The map did not contain the cardId, BAD REQUEST
                throw new BadRequestException("Provided cardId was not found stage area:  "+this.stageNum);
            }
        }
        return card;
    }
    /**
     * Helper method to remove a card from the PlayerPlayArea and all its tracking data structures.
     *
     * Cards do not unregister themselves, they are removed by this function when they leave the play
     * area for any reason.
     * @param card The card to be removed
     * @return True if the card was found and removed, False otherwise
     */
    private boolean removeCard(AdventureCards card) {
        if(card==null) {
            return false;
        }

        AdventureCards delCard = allCards.get(card.cardCode);

        if(delCard!=card) {
            return false;
        }
        if(stageCard != null && delCard.getCardID() == stageCard.getCardID()){
            stageCard = null;
        }

        AllCardCodes cardCode = card.getCardCode();
        allCards.remove(cardCode);
        cardIdMap.remove(card.getCardID());
        cardBoostDependencies.remove(cardCode);
        cardsWithBattleValue.remove(delCard);
        boostableCards.remove(delCard);
        return true;
    }

//    public void onPlayAreaChanges(StagePlayAreas targetPlayArea){
//        if(phaseController == null){
//            throw new CardAreaException(CardAreaException.CardAreaExceptionReasonCodes.GAMEPHASE_NOT_REGISTERED);
//        }
//        this.targetPlayArea=targetPlayArea;
//    }
//

    public boolean discardAllCards() {
        HashSet<AdventureCards> cardList = new HashSet<>(allCards.values());
        return discardCards(cardList);

    }


    @Override
    public void discardCard(AdventureCards card){
        HashSet<AdventureCards> cardList = new HashSet<>();
        cardList.add(card);
        discardCards(cardList);
    }

    /**
     * Discards all cards from play area in list.
     *
     * @sideEffects Card discard function will trigger all observing boosted cards to clear boost status.
     * @param cardList The list of cards to be discarded
     * @return True if at least one card was discarded
     */
    private boolean discardCards(HashSet<AdventureCards> cardList)
    {
        HashSet<AdventureCards> list = new HashSet<>(cardList);
        boolean rc = !list.isEmpty();
        for(AdventureCards card : list) {
            card.discardCard();
            removeCard(card);
        }
        cardList.clear();
        updateBattlePoints();
        return rc;
    }

    @Override
    public void onGameReset(){
        allCards.clear();
        cardBoostDependencies.clear();
        cardsWithBattleValue.clear();
        boostableCards.clear();
        bids=0;
        battlePoints=0;
        questCard=null;
        stageCard=null;
        targetPlayArea=null;
        //updateBattlePoints();
    }

    @Override
    public int getBattlePoints(){return battlePoints;}

    @Override
    public int getBids(){return bids;}

    @Override
    public void onGamePhaseEnded(){
        throw new UnsupportedOperationException();
    }

    public int size() {
        return allCards.size();
    }

    public void registerBattlePointContributor(BattlePointContributor card){
        cardsWithBattleValue.add(card);
        updateBattlePoints();
        notifyStageAreaChanged();
    }

    public void registerMinBid(TestCards card){
        bids = card.getMinimumBids();
    }

    @Override
    public void registerBoostableCard(BoostableCard card) {
        boostableCards.add(card);
        if(questCard.getBoostedFoe() == card.getCardCode()){
            card.setBoosted(true);
        }
    }


    /**
     * Recalculates the battlepoint value based on cards in the play area and the player rank.
     */
    private void updateBattlePoints() {
        int newBattlePoints=0;

        for(BattlePointContributor card : cardsWithBattleValue) {
            newBattlePoints+=card.getBattlePoints();
        }
        battlePoints=newBattlePoints;
        notifyStageAreaChanged();
    }

    public StageAreaData getStageAreaData() {
        HashSet<CardTypes> allowedTypes = new HashSet<>();
        CardData stageCardData = null;

        if (this.stageCard != null) {
            stageCardData = this.stageCard.generateCardData();
        }

        allowedTypes.add(CardTypes.FOE);
        allowedTypes.add(CardTypes.WEAPON);
        StageAreaData data = new StageAreaData(
                stageID,
                stageNum,
                bids,
                battlePoints,
                allowedTypes,
                stageCardData,
                getCardData()
        );
        return data;
    }

    public StageAreaData getObfuscatedStageAreaData() {
        HashSet<CardTypes> allowedTypes = new HashSet<>();
        CardData stageCardData = null;

        if (this.stageCard != null) {
            stageCardData = this.stageCard.generateObfuscatedCardData();
        }

        allowedTypes.add(CardTypes.FOE);
        allowedTypes.add(CardTypes.WEAPON);
        StageAreaData data = new StageAreaData(
                stageID,
                stageNum,
                0,
                0,
                allowedTypes,
                stageCardData,
                getObfuscatedCardData()
        );
        return data;
    }

    public ArrayList<CardData> getCardData() {
        ArrayList<CardData> handCards = new ArrayList<>();
        for(Cards card : allCards.values()) {
            if(this.stageCard == null || card.cardCode != stageCard.cardCode){
                handCards.add(card.generateCardData());
            }
        }
        return handCards;
    }

    public ArrayList<CardData> getObfuscatedCardData() {
        ArrayList<CardData> handCards = new ArrayList<>();
        for(Cards card : allCards.values()) {
            if(this.stageCard == null || card.cardCode != stageCard.cardCode){
                handCards.add(card.generateObfuscatedCardData());
            }
        }
        return handCards;
    }

    /**
     * Updates the clients about a stage area being changed, sending the new state.
     */
    public void notifyStageAreaChanged() {
        phaseController.notifyStageAreaChanged(this,getStageAreaData(),getObfuscatedStageAreaData());
    }

    @Override
    public void onEffectResolved(CardWithEffect resolvedCard) {
        phaseController.testResolved();
    }

    @Override
    public void onEffectResolvedWithDelayedTrigger(CardWithEffect resolvedCard) {
        //not used
    }
}

