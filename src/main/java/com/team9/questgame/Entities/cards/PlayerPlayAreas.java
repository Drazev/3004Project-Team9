package com.team9.questgame.Entities.cards;

import com.fasterxml.jackson.annotation.*;
import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Data.CardData;
import com.team9.questgame.Data.PlayAreaData;
import com.team9.questgame.Data.PlayAreaDataSources;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.exception.IllegalEffectStateException;
import com.team9.questgame.exception.CardAreaException;
import com.team9.questgame.exception.IllegalGamePhaseStateException;
import com.team9.questgame.game_phases.GamePhases;
import com.team9.questgame.gamemanager.service.OutboundService;
import org.slf4j.Logger;

import java.util.*;

import static com.team9.questgame.exception.CardAreaException.CardAreaExceptionReasonCodes.GAMEPHASE_NOT_REGISTERED;
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
    private GamePhases phaseController;
    @JsonIgnore
    private final OutboundService outboundService;
    @JsonIgnore
    private Logger LOG;
    private PlayAreas<AdventureCards> targetPlayArea;
    private int bids;
    private int battlePoints;
    private HashMap<CardTypes, HashSet<AdventureCards>> cardTypeMap;
    private HashMap<AllCardCodes,AdventureCards> allCards;
    private QuestCards questCard;
    private HashSet<CardTypes> playableCardTypes;

    //Managed by Registration methods and triggered by cards
    private HashMap<AllCardCodes,HashSet<BoostableCard>> cardBoostDependencies;
    private HashSet<CardWithEffect> cardsWithActiveEffects;
    private HashSet<BattlePointContributor> cardsWithBattleValue;
    private HashSet<BidContributor> cardsWithBidValue;

    static private long nextid=0;

    public PlayerPlayAreas(Players player) {
        this.id = nextid++;
        this.player = player;
        cardTypeMap = new HashMap<>();
        allCards = new HashMap<>();
        questCard=null;
        playableCardTypes = new HashSet<>();
        cardBoostDependencies = new HashMap<>();
        cardsWithActiveEffects = new HashSet<>();
        phaseController = null;
        cardsWithBattleValue = new HashSet<>();
        cardsWithBidValue = new HashSet<>();
        targetPlayArea=null;
        bids=0;
        battlePoints=0;

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

    /**
     * Creates a CardData record for each card in the play area.
     * @return A list of CardData elements representing the cards in the play area
     */
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

    public boolean discardAllCards() {
        HashSet<AdventureCards> cardList = new HashSet<>(allCards.values());
        return discardCards(cardList);

    }

    public HashMap<CardTypes, HashSet<AdventureCards>> getCardTypeMap() {
        return cardTypeMap;
    }


    public boolean discardAllAllies() {
        HashSet<AdventureCards> cardList = cardTypeMap.get(CardTypes.ALLY);
        return discardCards(cardList);

    }

    public boolean discardAllWeapons() {
        HashSet<AdventureCards> cardList = cardTypeMap.get(CardTypes.WEAPON);
        return discardCards(cardList);
    }

    public boolean discardAllAmour() {
        HashSet<AdventureCards> cardList = cardTypeMap.get(CardTypes.AMOUR);
        return discardCards(cardList);
    }

    /**
     * Discards a card both returning it to the deck in the discard pile and
     * removing it from the play area.
     * @param card The card to be discarded
     */
    @Override
    public void discardCard(AdventureCards card) {
        HashSet<AdventureCards> cardList = new HashSet<>();
        cardList.add(card);
        discardCards(cardList);
    }

    /**
     * Add a card to the Players In play area.
     * Cards are received by Players Hand when they
     * attempt to play a card from their hand.
     * @param card The card that is to be received into the PlayerPlayArea
     * @return True if successfully played, false otherwise
     * @throws CardAreaException The PlayerPlayArea rejected this request with a reason, or reached an unexpected state
     */
    @Override
    public boolean receiveCard(AdventureCards card) throws CardAreaException {
        if(card==null) {
            throw new CardAreaException(CardAreaException.CardAreaExceptionReasonCodes.NULL_CARD);
        }

        if(!playableCardTypes.contains(card.getSubType())) {
            //RULE: If hand is oversize the player can discard a card or play Ally card
            if( !( card.getSubType()==CardTypes.ALLY && player.getHand().isHandOversize() ) ) {
                throw new CardAreaException("Card {"+card.getCardCode()+","+card.getSubType()+"} cannot be played at this time.", CardAreaException.CardAreaExceptionReasonCodes.CARD_TYPE_CANNOT_BE_PLAYED_AT_THIS_TIME);
            }
        }

        if(card.getSubType()==CardTypes.FOE || card.getSubType()==CardTypes.TEST) {
            if(targetPlayArea==null) {
                throw new CardAreaException("A FOE or TEST card was played, but targetPlayArea was null. When FOE and TEST cards are allowed the targetPlayArea should never be null!",CardAreaException.CardAreaExceptionReasonCodes.UNEXPECTED_STATE);
            }
            return playCard(card);
        }

        return addToPlayArea(card);
    }


    /**
     * Attempt to play a card into target play area. This is mainly used
     * for cards played from hand into the Active Game Phase
     * @param card The card to be played into the active game phase
     * @return True if the card was accepted, False otherwise
     */
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
        boolean anyCardBoostChanged=false;
        list.add(card);
        AdventureCards triggerCard = allCards.get(triggerCardCode);
        if(triggerCard!=null)
        {
            triggerCard.registerBoostedCard(card);
            card.setBoosted(true);
            anyCardBoostChanged=true;
        }

        if(questCard!=null && triggerCardCode==questCard.getCardCode()) {
            card.setBoosted(true);
            anyCardBoostChanged=true;
        }

        if(anyCardBoostChanged) {
            update();
        }
    }

    /**
     * Register a card as having a bid value. These cards will contribute to the play area's
     * bid calculation including it's value.
     * @param card
     */
    public void registerBidContributor(BidContributor card) {
        cardsWithBidValue.add(card);
        updateBids();
        notifyPlayAreaChanged();
    }

    /**
     * Register a card as having a battle point value. These cards will contribute
     * to the play area's battle point calculation and it's total value
     * @param card A card containing a battlepoint value
     */
    public void registerBattlePointContributor(BattlePointContributor card) {
        cardsWithBattleValue.add(card);
        updateBattlePoints();
        notifyPlayAreaChanged();
    }

    /**
     * Register an card to enable it's activation from the play area
     * @param card A card in the play area that can be activated
     * @return True if registered, False otherwise
     */
    public boolean registerActiveEffect(CardWithEffect card) {
        return cardsWithActiveEffects.add(card);
    }

    /**
     * Game Phase Controller registers themselvef as the active game controller.
     * This will enable certain features.
     * @param activePhase The active game phase to be set
     */
    public void registerGamePhase(GamePhases activePhase) {
        if(activePhase==null)
        {
            throw new IllegalGamePhaseStateException(null,NULL_ACTIVE_PHASE);
        }
        this.phaseController=activePhase;
    }

    /**
     * Activate the card effect and then discard it
     * @param cardId The card ID to be activated if found
     * @return True if effect successfully activated, False otherwise
     * @throws IllegalEffectStateException Rethrown from Effects if an exception occurs during execution
     */
    public boolean activateCard(long cardId) throws IllegalEffectStateException {

        for(CardWithEffect card : cardsWithActiveEffects) {
            if(card.getCardID()==cardId) {
                card.activate(player);
                card.discardCard();
                cardsWithActiveEffects.remove(card);
                return true;
            }
        }

       return false;
    }

    /**
     * To be called by Game Phase Controllers when a game phase ends.
     * This will clear Game Phase properties and enable restrictions on
     * what card can be played to be aligned with the general phase
     */
    @Override
    public void onGamePhaseEnded() {
        if(phaseController==null) {
            throw new CardAreaException(GAMEPHASE_NOT_REGISTERED);
        }
        //Unset Quest boost if quest card was set
        if(cardBoostDependencies.containsKey(questCard.getCardCode())) {
            for(BoostableCard boostCard : cardBoostDependencies.get(questCard.getCardCode())) {
                boostCard.setBoosted(false);
            }
        }
        phaseController=null;
        targetPlayArea=null;
        questCard=null;
        discardAllAmour();
        discardAllWeapons();
        playableCardTypes.clear();
        update();
    }

    /**
     * Can be called to return the player play area so a new game can be played
     */
    @Override
    public void onGameReset() {
        cardTypeMap.clear();
        allCards.clear();
        cardBoostDependencies.clear();
        cardsWithActiveEffects.clear();
        phaseController = null;
        cardsWithBattleValue.clear();
        cardsWithBidValue.clear();
        bids=0;
        battlePoints=0;
        questCard=null;
        targetPlayArea=null;
        playableCardTypes.clear();
    }

    /**
     * This is to be called by Game Phase controllers in order to enable players
     * to play cards into a stage PlayArea. This is generally only active for the quest sponsor.
     * @param targetPlayArea An active play area representing a game stage for a game phase
     */
    public void onPlayAreaChanged(PlayAreas targetPlayArea) {
        if(phaseController==null) {
            throw new CardAreaException(GAMEPHASE_NOT_REGISTERED);
        }

        this.targetPlayArea=targetPlayArea;
        if(targetPlayArea!=null) {
            playableCardTypes.add(CardTypes.FOE);
            playableCardTypes.add(CardTypes.TEST);
        }
        else {
            playableCardTypes.remove(CardTypes.FOE);
            playableCardTypes.remove(CardTypes.TEST);
        }
    }

    /**
     * Called by the Game Phase controller to declare the active game phase a quest phase.
     * This will trigger boost conditions on cards that are quest boosted.
     *
     * It is cleared when onGamePhaseEnded() is called
     * @param questCard
     */
    public void onQuestStarted(QuestCards questCard) {
        if(phaseController==null) {
            throw new CardAreaException(GAMEPHASE_NOT_REGISTERED);
        }

        this.questCard=questCard;

        //Find any cards that are boosted by quest and set boost
        if(cardBoostDependencies.containsKey(questCard.getCardCode())) {
            for(BoostableCard boostCard : cardBoostDependencies.get(questCard.getCardCode())) {
                boostCard.setBoosted(true);
            }
        }

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

        AllCardCodes cardCode = card.getCardCode();
        allCards.remove(cardCode);
        cardTypeMap.get(cardCode.getSubType()).remove(card);

        cardBoostDependencies.remove(cardCode);
        cardsWithActiveEffects.remove(delCard);
        cardsWithBattleValue.remove(delCard);
        cardsWithBidValue.remove(delCard);
        return true;
    }

    /**
     * Updates all the PlayerPlayArea's properties, recalculating the attributes.
     * Call this when any card is added,removed, or has had it's boost state changed.
     *
     * This will trigger a client update.
     */
    public void update() {
        updateBids();
        updateBattlePoints();
        notifyPlayAreaChanged();
    }

    /**
     * Recalculates the bid value based on cards in the play area.
     */
    private void updateBids() {
        int newBids=0;

        for(BidContributor card : cardsWithBidValue) {
            newBids+=card.getBids();
        }

        bids=newBids;
    }
    /**
     * Recalculates the battlepoint value based on cards in the play area and the player rank.
     */
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
        update();
        return rc;
    }

    /**
     * Helper function to facilitate adding a card to the PlayerPlayArea and keeping all tracking structures
     * coordinated.
     * @param card The card to be added to the play area
     * @return True if the card was added, false otherwise.
     * @throws CardAreaException Thrown when the card to be added is a duplicate of another card already in the play area
     */
    private boolean addToPlayArea(AdventureCards card) throws CardAreaException {
        //GameRule:: Cannot play two of the same card
        if(allCards.containsKey(card.getCardCode())) {
            LOG.error("RULE: Player "+player.getName()+" cannot have more than two cards of the same type in play.");
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

        update();
        return true;
    }

    /**
     * Updates the clients about a play area being changed, sending the new state.
     */
    private void notifyPlayAreaChanged() {
        outboundService.broadcastPlayAreaChanged(player,getPlayAreaData());
    }

    /**
     * Used by the Game Phase Controller to signify a players turn. If the PlayerPlayArea's player
     * matches the current player then some card types are enabled. Otherwise those card types are disabled.
     *
     * RULE: Players must only play cards on their turn unless the hand is oversize, and they are forced to play an Ally card.
     * @param player The player whom has the active turn
     */
    public void onPhaseNextPlayerTurn(Players player) {
        if(phaseController==null) {
            throw new CardAreaException(GAMEPHASE_NOT_REGISTERED);
        }

        if(player == this.player && phaseController!=null) {
            playableCardTypes.add(CardTypes.ALLY);
            playableCardTypes.add(CardTypes.WEAPON);
            playableCardTypes.add(CardTypes.AMOUR);
        }
        else {
            playableCardTypes.remove(CardTypes.ALLY);
            playableCardTypes.remove(CardTypes.WEAPON);
            playableCardTypes.remove(CardTypes.AMOUR);
        }
    }
}
