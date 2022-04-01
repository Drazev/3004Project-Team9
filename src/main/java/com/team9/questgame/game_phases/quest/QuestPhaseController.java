package com.team9.questgame.game_phases.quest;

import com.team9.questgame.Data.PlayerData;
import com.team9.questgame.Data.StageAreaData;
import com.team9.questgame.Entities.Effects.CardEffects.TestEndEffect;
import com.team9.questgame.Entities.Effects.EffectResolverService;
import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.*;
import com.team9.questgame.exception.IllegalGameRequest;
import com.team9.questgame.exception.SponsorAlreadyExistsException;
import com.team9.questgame.game_phases.GamePhases;
import com.team9.questgame.game_phases.GeneralGameController;
import com.team9.questgame.exception.IllegalQuestPhaseStateException;
import com.team9.questgame.game_phases.GeneralStateMachine;
import com.team9.questgame.game_phases.utils.PlayerTurnService;
import com.team9.questgame.gamemanager.record.socket.NotificationOutbound;
import com.team9.questgame.gamemanager.record.socket.QuestEndedOutbound;
import com.team9.questgame.gamemanager.record.socket.RemainingQuestorsOutbound;
import com.team9.questgame.gamemanager.service.NotificationOutboundService;
import com.team9.questgame.gamemanager.service.QuestPhaseInboundService;
import com.team9.questgame.gamemanager.record.socket.RequestBidOutbound;
import com.team9.questgame.gamemanager.service.QuestPhaseOutboundService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class QuestPhaseController implements GamePhases<QuestCards,QuestPhaseStatesE> {
    Logger LOG;

    @Getter
    private final QuestPhaseStateMachine stateMachine;

    private final QuestCards questCard;

    private GeneralGameController generalController;

    @Getter
    private CopyOnWriteArrayList<Players> questingPlayers;
    @Getter
    private PlayerTurnService playerTurnService;
//    private PlayerTurnService joinTurnService;
    @Getter
    private ArrayList<StagePlayAreas> stages;
//    private StagePlayAreas newStage;
    private HashMap<StagePlayAreas,HashSet<Players>> stageVisibleToPlayersList;
    private Players maxBidPlayer;
    private int maxBid;
    private int bidCount;
    private HashSet<Long> cardIDUsedToRevealStage;

    @Getter
    private Players sponsor;
    @Getter
    private int sponsorAttempts;
    @Getter
    private int joinAttempts;
    @Getter
    private int numParticipants;
    @Getter
    private int participantSetupResponses;
    @Getter
    private int curStageIndex;
    //private boolean minJoin;
    @Getter
    private boolean nextStageTest;
    @Getter
    private boolean stagesAreValid;


    public QuestPhaseController(GeneralGameController gameController, QuestCards card) {
        LOG = LoggerFactory.getLogger(QuestPhaseController.class);
        this.generalController = gameController;
        this.questCard = card;
        stateMachine = new QuestPhaseStateMachine(this);
        GeneralStateMachine.getService().registerObserver(stateMachine);
        this.questingPlayers = new CopyOnWriteArrayList<>();
        this.stages = new ArrayList<>();
        stageVisibleToPlayersList = new HashMap<>();
        cardIDUsedToRevealStage = new HashSet<>();
        playerTurnService = null;
        sponsor = null;
        sponsorAttempts = 0;
        numParticipants = 0;
        //minJoin = false;
        stagesAreValid = false;
        nextStageTest = false;
        maxBidPlayer = null;
        maxBid=0;
        QuestPhaseInboundService.getService().setQuestController(this);
    }

    @Override
    public QuestPhaseStatesE getCurrState() {
        return stateMachine.getCurrentState();
    }

    /**
     * Starts the quest phase
     * Assume that the phase has already been reset (NOT_STARTED state)
     * @param playerTurnService A turn service in which the first turn is the player who drawn the Quest Card
     */
    @Override
    public void startPhase(PlayerTurnService playerTurnService) {
        if (stateMachine.isQuestStarted()) {
            throw new IllegalQuestPhaseStateException("Cannot start quest phase when the Quest has already started");
        } else if (questCard == null) {
            throw new RuntimeException("Cannot start quest phase, questCard is null");
        } else if (stateMachine.isBlocked()) {
            throw new IllegalGameRequest("Cannot start phase when the game is blocked", playerTurnService.getPlayerTurn());
        }
        else if (playerTurnService==null) {
            throw new IllegalQuestPhaseStateException("Quest Phase recieved a null playerTurnService and cannot start");
        }
        LOG.info("Quest phase started");
        this.playerTurnService = playerTurnService;
        registerPlayerPlayAreas(questCard);
        QuestPhaseOutboundService.getService().broadcastSponsorSearch(playerTurnService.getPlayerTurn().generatePlayerData());
        stateMachine.setPhaseStartRequested(true);
        stateMachine.update();
    }

    /**
     * Check client sponsor result
     * @param player the player who made the sponsor request
     * @param found true if the player decides to sponsor(sponsor found) false otherwise
     */
    public void checkSponsorResult(Players player, boolean found){
        if(stateMachine.getCurrentState() != QuestPhaseStatesE.QUEST_SPONSOR){
            throw new IllegalQuestPhaseStateException("Cannot check sponsor result when not in QUEST_SPONSOR state" );
        } else if (player.getPlayerId() != playerTurnService.getPlayerTurn().getPlayerId()) {
            throw new IllegalGameRequest("Must be current player turn to send sponsor result", player);
        } else if (stateMachine.isBlocked()) {
            throw new IllegalGameRequest("Cannot check sponsor when the game is blocked", player);
        }

        // Increment sponsorAttempts counter so that the state machine knows when we run out of attempts
        this.sponsorAttempts++;

        if(found){
            if(this.sponsor != null){
                throw new SponsorAlreadyExistsException(player);
            } else if(validateSponsor(player)){
                this.sponsor = player;
            }

        }

        else {
            playerTurnService.nextPlayer();
        }

        stateMachine.update();
//        switch (stateMachine.getCurrentState()) {
//            case QUEST_SPONSOR -> {
//                // Attempts sponsor search on the next player
//                playerTurnService.nextPlayer();
//                QuestPhaseOutboundService.getService().broadcastSponsorSearch(playerTurnService.getPlayerTurn().generatePlayerData());
//            } case QUEST_SETUP -> {
//                QuestPhaseOutboundService.getService().broadcastSponsorFound(sponsor.generatePlayerData());
//                this.setupStage();
//            } case ENDED -> {
//                this.endPhase();
//            } default -> {
//                throw new IllegalQuestPhaseStateException("Unknown state");
//            }
//        }
    }

    /**
     * Called by checkSponsorResult() after a sponsor is found and the quest is set up for the first time
     */
    private void setupStage(){
        //old way
//        newStage = new StagePlayAreas(questCard, sponsor, stages.size());
//        sponsor.getPlayArea().onPlayAreaChanged(newStage);
//        QuestPhaseOutboundService.getService().broadcastSponsorSetup(sponsor.generatePlayerData());

        //make as many stages as are stages in the quest and add them to the array
        this.sponsor.getPlayArea().setSponsorMode(true);
        this.stageVisibleToPlayersList.clear();
        for(int i = 0; i < questCard.getStages(); i++) {
            stages.add(new StagePlayAreas(this, questCard, sponsor, i));
            stages.get(i).notifyStageAreaChanged();
            HashSet<Players> temp = new HashSet<>();
            temp.add(this.sponsor);
            stageVisibleToPlayersList.put(stages.get(i),temp);
        }
        EffectResolverService.getService().onQuestPhaseStarted(this);
        this.cardIDUsedToRevealStage.clear();
        stateMachine.update();

    }

    /**
     *
     * Sponsor informs that the quest setup is complete
     * @param player the player who sent the request
     * @return true if the setup is accepted, false otherwise
     */
    public boolean questSetupComplete(Players player){
        if(stateMachine.getCurrentState() != QuestPhaseStatesE.QUEST_SETUP){
            throw new IllegalQuestPhaseStateException("Cannot set up a stage when not in QUEST_SETUP phase");
        } else if (player.getPlayerId() != this.sponsor.getPlayerId()) {
            throw new IllegalGameRequest("Only the sponsor can setup the quest", player);
        } else if(stateMachine.isBlocked()){
            throw new IllegalGameRequest("Cannot proceed when the quest phase is blocked", player);
        }

        // TODO: pass in an array of new StagesPlayArea to the validateStageSetup function
        if (!validateStageSetup(stages)) {
            LOG.info("Stage Validation failed");
            return false;
        }
        stagesAreValid = true;
        curStageIndex=0;
        //sponsor should no longer be able to play cards after stage setup
        sponsor.getPlayArea().setSponsorMode(false);
        sponsor.getPlayArea().onStageChanged(null);

        stateMachine.update();

//        switch (stateMachine.getCurrentState()) {
//            case QUEST_SETUP -> {
//                QuestPhaseOutboundService.getService().broadcastSponsorFound(sponsor.generatePlayerData());
//                this.setupStage();
//            } case QUEST_JOIN -> {
//                QuestPhaseOutboundService.getService().broadcastJoinRequest();
//            } default -> {
//                throw new IllegalQuestPhaseStateException("Unknown state");
//            }
//        }
        return true;
    }

    /**
     * Used by stageSetupComplete() Validate if the new stages' setup are valid
     * @param newStages
     * @return
     */
    private boolean validateStageSetup(ArrayList<StagePlayAreas> newStages) {

        // Check if subsequent stages have increasing battlePoint
        int minBattlePoint = 0;
        for (StagePlayAreas stage: newStages) {
            if (stage.getBattlePoints() <= minBattlePoint && stage.getStageCard()!=null && stage.getStageCard().getSubType() != CardTypes.TEST) {
                return false;
            }
            minBattlePoint = stage.getBattlePoints();
        }

        // Check if each stage contain exactly 1 foe and unique weapons
        for (StagePlayAreas stage: newStages) {
            ArrayList<FoeCards> foeCards = new ArrayList<>();
            ArrayList<WeaponCards> weaponCards = new ArrayList<>();
            ArrayList<TestCards> testCards = new ArrayList<>();

            HashMap<AllCardCodes, AdventureCards> allCards = stage.getAllCards();
            for (AdventureCards card: allCards.values()) {
                if (card.getSubType() == CardTypes.FOE) {
                    foeCards.add((FoeCards) card);
                } else if (card.getSubType() == CardTypes.QUEST) {
                    weaponCards.add((WeaponCards) card);
                }else if(card.getSubType() == CardTypes.TEST){
                    testCards.add((TestCards) card);
                }
            }
            if (foeCards.size() != 1 && testCards.size() != 1) {
                // There must be exactly 1 foe in the stage or 1 test stage
                return false;
            }else if(foeCards.size() > 0 && testCards.size() > 0){
                return false;
            }else {
                // All weapon cards must be unique
                for (WeaponCards thisCard: weaponCards) {
                    for (WeaponCards otherCard: weaponCards) {
                        if (thisCard.getCardID() != otherCard.getCardID() && thisCard.getCardName() == otherCard.getCardName()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean checkForTest(){
        if(curStageIndex >= questCard.getStages() || stages.get(curStageIndex).getStageCard()==null){
            return false;
        }
        return (stages.get(curStageIndex).getStageCard().getSubType() == CardTypes.TEST);

    }

    /**
     * Player's decision to join a quest stage or not
     * @param player the player who sent this request
     * @param joined true if they want to join this stage, false otherwise
     */
    public void checkJoinResult(Players player, boolean joined){
        if (stateMachine.getCurrentState() != QuestPhaseStatesE.QUEST_JOIN) {
            throw new IllegalQuestPhaseStateException("A player can only join when the quest is in QUEST_JOIN state");
        } else if (player.getPlayerId() == sponsor.getPlayerId()) {
            throw new IllegalGameRequest("The sponsor cannot join quest stages", player);
        }

        // Increment this counter so that the stateMachine knows when all players replied
        this.joinAttempts++;

        if(joined){
            //minJoin = true;
            questingPlayers.add(player);
        }

        checkForTest();
        stateMachine.update();
//        switch (stateMachine.getCurrentState()) {
//            case QUEST_JOIN -> {
//                // Do nothing since the broadcast already sent to all players
//            } case IN_TEST -> {
//                testSetup();
//            } case PARTICIPANT_SETUP -> {
//                participantSetup();
//                //resolveStage(0);
//            } case ENDED -> {
//                endPhase();
//            } default -> {
//                throw new IllegalQuestPhaseStateException("Unknown state");
//            }
//        }
    }

    private void testSetup(){
        //dealAdventureCard();
        while(playerTurnService.getPlayerTurn().getPlayerId() == sponsor.getPlayerId() || !questingPlayers.contains(playerTurnService.getPlayerTurn())){
            playerTurnService.nextPlayer();
        }
        QuestPhaseOutboundService.getService().broadcastTestStageStart(new RemainingQuestorsOutbound(generateQuestorData(), curStageIndex));
        maxBid = stages.get(curStageIndex).getBids() > 0 ? stages.get(curStageIndex).getBids()-1 : 0; //The first player can bid the min bid value. Pretend we already have a bid of minBids-1 or zero if there is no min bid.
        maxBidPlayer=null;
        QuestPhaseOutboundService.getService().broadcastRequestBid(new RequestBidOutbound(playerTurnService.getPlayerTurn().generatePlayerData(), maxBid, null));
        StagePlayAreas currStage = stages.get(curStageIndex);
        HashSet<Players> pList=stageVisibleToPlayersList.get(currStage);
        if(pList==null) {
            throw new IllegalQuestPhaseStateException(String.format("Player visibility list couldn't find stage %d.",curStageIndex+1));
        }
        pList.addAll(playerTurnService.getPlayers()); //Make stage visible to all players on resolution
        currStage.notifyStageAreaChanged();
        for(Players p : playerTurnService.getPlayers()) {
            p.getPlayArea().setPlayerTurn(true);
            p.getPlayArea().setQuestTestMode(true);
        }

    }

    public void checkTestBidResponse(Players player, int bid){
        if(stateMachine.getCurrentState() != QuestPhaseStatesE.IN_TEST){
            throw new IllegalQuestPhaseStateException("you can only bid when the game is in the IN_TEST state");
        }
        if(player.getPlayerId() != playerTurnService.getPlayerTurn().getPlayerId()){
            throw new IllegalGameRequest("It must be the players turn for them to bid", player);
        }
        else if(bid<0) {
            if(maxBidPlayer == null){
                QuestPhaseOutboundService.getService().broadcastRequestBid(new RequestBidOutbound(playerTurnService.getPlayerTurn().generatePlayerData(), maxBid, null));
            }
            else{
                QuestPhaseOutboundService.getService().broadcastRequestBid(new RequestBidOutbound(playerTurnService.getPlayerTurn().generatePlayerData(), maxBid, maxBidPlayer.generatePlayerData()));
            }
        }

        int totBid = bid+player.getPlayArea().getBids();

        //Check for disqualification. Disqualified if you don't bid more than then the current bid;
        if(totBid <= maxBid) {
            questingPlayers.remove(player);
            NotificationOutboundService.getService().sendBadNotification(
                    sponsor,
                    new NotificationOutbound("Test Stage Defeat","Your bid was deemed unworthy. Alas, you cannot continue your journey and must head home.","",null),
                    null
            );
        }
        else {
            //Check to make sure player can honor their bid (Had enough Cards in hand)
            if(player.getHand().getHandSize() < bid) {
                NotificationOutboundService.getService().sendBadNotification(player,
                        new NotificationOutbound("Bid Too Large", "You bid more cards than you can discard, try again","",null),
                        null
                );
                if(maxBidPlayer == null){
                    QuestPhaseOutboundService.getService().broadcastRequestBid(new RequestBidOutbound(playerTurnService.getPlayerTurn().generatePlayerData(), maxBid, null));
                }
                else{
                    QuestPhaseOutboundService.getService().broadcastRequestBid(new RequestBidOutbound(playerTurnService.getPlayerTurn().generatePlayerData(), maxBid, maxBidPlayer.generatePlayerData()));
                }
                return;
            }
            else {
                NotificationOutboundService.getService().sendGoodNotification(
                        sponsor,
                        new NotificationOutbound("Bid Accepted","Your bid was accepted.","",null),
                        null
                );
                maxBidPlayer = player;
                maxBid = bid;
                player.getPlayArea().setPlayerTurn(false);
            }

        }

        if(questingPlayers.size() <= 1){
            QuestPhaseOutboundService.getService().broadcastStageResult(new RemainingQuestorsOutbound(generateQuestorData(), curStageIndex));
            //TODO:make maxBidPlayer discard maxBid-maxBidPlayer.getPlayerPlayArea().getBattlePoints() cards
            curStageIndex++;
            NotificationOutboundService.getService().sendInfoNotification(
                    sponsor,
                    new NotificationOutbound("Test Stage Bidding Closed","A winner has been chosen! They must now honor their bids by discarding the necessary cards!","",null),
                    null
            );
            for(Players p : playerTurnService.getPlayers()) {
                p.getPlayArea().setPlayerTurn(false);
                p.getPlayArea().setQuestTestMode(false);
            }
            if(maxBidPlayer!=null) {
                Effects testEnd = new TestEndEffect(maxBidPlayer, maxBid);
                testEnd.setSource((CardWithEffect) stages.get(curStageIndex).getStageCard());
                testEnd.activate(stages.get(curStageIndex), maxBidPlayer);
            }
            else {
                testResolved();
            }
        }
        else{
            do{
                playerTurnService.nextPlayer();
            }
            while(playerTurnService.getPlayerTurn().getPlayerId() == sponsor.getPlayerId() || !questingPlayers.contains(playerTurnService.getPlayerTurn()));

            playerTurnService.getPlayerTurn().getPlayArea().setPlayerTurn(true);
            if(maxBidPlayer == null){
                QuestPhaseOutboundService.getService().broadcastRequestBid(new RequestBidOutbound(playerTurnService.getPlayerTurn().generatePlayerData(), maxBid, null));
            }else{
                QuestPhaseOutboundService.getService().broadcastRequestBid(new RequestBidOutbound(playerTurnService.getPlayerTurn().generatePlayerData(), maxBid, maxBidPlayer.generatePlayerData()));
            }
        }
    }

    public void testResolved(){
        if(maxBidPlayer!=null) {
            NotificationOutboundService.getService().sendGoodNotification(
                    maxBidPlayer,
                    new NotificationOutbound("Test Stage Victory","You have successfully overcome the test!","",null),
                    null
            );
            maxBidPlayer=null;
        }
        checkForTest();
        stateMachine.update();
    }

    private void participantSetup(){
//        dealAdventureCard();
        //TODO: for all players allow them to play cards via player.getPlayerArea().onPhaseNextPlayerTurn(player)
        LOG.info(String.format("STARTING A NEW STAGE: STAGE %d", curStageIndex+1));
        for(Players player : playerTurnService.getPlayers()){
            player.getPlayArea().setPlayerTurn(questingPlayers.contains(player));
        }
        QuestPhaseOutboundService.getService().broadcastFoeStageStart(new RemainingQuestorsOutbound(generateQuestorData(), curStageIndex));
    }

    /**
     * A quest participant informs that their stage setup is complete
     */
    public void checkParticipantSetup(Players player){
        if (stateMachine.getCurrentState() != QuestPhaseStatesE.PARTICIPANT_SETUP) {
            throw new IllegalQuestPhaseStateException("A player can only setup their area when the quest is in PARTICIPANT_SETUP state");
        }

        participantSetupResponses++;
        player.getPlayArea(). setPlayerTurn(false);
        stateMachine.update();
    }

    private void resolveStage(int stageNum){
        StagePlayAreas currStage = stages.get(curStageIndex);
        if(currStage==null) {
            throw new IllegalQuestPhaseStateException(String.format("Quest stage index %d could not be found for stage %d!",curStageIndex,curStageIndex+1));
        }
        HashSet<Players> pList=stageVisibleToPlayersList.get(currStage);
        if(pList==null) {
            throw new IllegalQuestPhaseStateException(String.format("Player visibility list couldn't find stage %d.",curStageIndex+1));
        }
        pList.addAll(playerTurnService.getPlayers()); //Make stage visible to all players on resolution
        currStage.notifyStageAreaChanged();
        for(Players player:questingPlayers){
            player.getPlayArea().setPlayerTurn(false);
            if(player.getPlayArea().getBattlePoints()+1 < stages.get(stageNum).getBattlePoints()){
                NotificationOutboundService.getService().sendBadNotification(
                        sponsor,
                        new NotificationOutbound("Quest Stage Defeat","You have failed this stage of the quest. Alas, you cannot continue your journey and must head home.","",null),
                        null
                );
                questingPlayers.remove(player);
            }
            player.getPlayArea().discardAllWeapons();
        }

        QuestPhaseOutboundService.getService().broadcastStageResult(new RemainingQuestorsOutbound(generateQuestorData(), curStageIndex));

        participantSetupResponses=0;
        curStageIndex++;
        checkForTest();
        stateMachine.update();
    }

    /**
     * Helper function to generate player data on questing players
     */
    private ArrayList<PlayerData> generateQuestorData(){
        ArrayList<PlayerData> questorData = new ArrayList<>();
        for(Players player : questingPlayers){
            questorData.add(player.generatePlayerData());
        }
        return questorData;
    }

    /**
     * Reset the phase
     */
    public void endPhase() {
        if (stateMachine.getCurrentState() != QuestPhaseStatesE.ENDED) {
            throw new IllegalQuestPhaseStateException("Cannot end phase when it's not in ENDED state");
        }
        QuestPhaseOutboundService.getService().broadcastQuestEnded(new QuestEndedOutbound(generateQuestorData()));

        for(Players p : this.playerTurnService.getPlayers()) {
            p.getPlayArea().onGamePhaseEnded();
        }
        for(StagePlayAreas stage : stages){
            stage.onGameReset();
        }
        this.stages.clear();
        this.stageVisibleToPlayersList.clear();
        this.cardIDUsedToRevealStage.clear();
        stateMachine.setPhaseReset(true);
        GeneralStateMachine.getService().unregisterObserver(stateMachine);
        QuestPhaseInboundService.getService().setQuestController(null);
        generalController.requestPhaseEnd();
    }

    @Override
    public QuestCards getCard() {
        return questCard;
    }

    private void registerPlayerPlayAreas(QuestCards card){
        for(Players player : playerTurnService.getPlayers()){
            player.getPlayArea().registerGamePhase(this);
            player.getPlayArea().onQuestStarted(card);
        }
    }

    private void distributeRewards(){

        HashMap<Players, Integer> participantRewards = new HashMap<>();
        for(Players player : questingPlayers){
            participantRewards.put(player, questCard.getStages());
        }
        EffectResolverService.getService().onQuestCompleted(participantRewards);
        int sumCards = 0;
        HashMap<Players, Integer> sponsorRewards = new HashMap<>();

        for(StagePlayAreas stage : stages){
            sumCards += stage.size();
        }
        sponsorRewards.put(sponsor, sumCards+questCard.getStages());
        EffectResolverService.getService().drawAdventureCards(sponsorRewards);
        stateMachine.update();
    }

    private void dealAdventureCard(){
        HashMap<Players,Integer> drawList = new HashMap<>();
        for(Players player : questingPlayers){
            drawList.put(player,1);
        }
        EffectResolverService.getService().drawAdventureCards(drawList);

        stateMachine.update();
//        switch(stateMachine.getCurrentState()) {
//            case PARTICIPANT_SETUP -> participantSetup();
//        }
    }

    public void sponsorPlayCard(Players player, long cardId, int src, int dst){
        if(!stateMachine.isInQuest()){
            throw new IllegalQuestPhaseStateException("Must be quest setup state to change stages");
        } else if(player.getPlayerId() != sponsor.getPlayerId()){
            throw new RuntimeException("Only the sponsor can play cards during quest setup");
        }
        LOG.info(String.format("Player %s play card %d from src=%d to dst=%d", player.getName(), cardId, src, dst));

        if(src < 0 && dst < 0){
            //play card to playerPlayArea
            //player.actionPlayCard(cardId);
            throw new UnsupportedOperationException("Not implemented");
        }else if (src < 0){

            //play card from hand to specific stage
            player.getPlayArea().onStageChanged(stages.get(dst));
            //if player plays nonFOE/WEAPON card it'll be played into their playerplayarea
            player.actionPlayCard(cardId);

            player.getPlayArea().onStageChanged(null);

        } else if( dst < 0){
            //withdraw card from stage to hand?
            stages.get(src).returnToHand(cardId);
        } else{
            //move card from stage to stage
            //stages.get((int)src).onPlayAreaChanges(stages.get((int)dst));
            stages.get(src).playCard(cardId, stages.get(dst));
        }
    }

    /**
     * Validate if a player can sponsor the quest
     * @param player
     * @return
     */
    private boolean validateSponsor(Players player){
        return true;
    }

    /**
     * Makes a stage visible to a specific player for the duration of this quest phase.
     * Card ID's can only be used once per quest.
     * @param stageID A unique identifier for the stage that should be revealed to the player
     * @param player A player which should see all the data available for a given stage.
     * @param cardIDUsed The cardID used to trigger this effect. It can only be used once per quest phase.
     * @return True if successful, False if it was denied
     */
    public boolean makeStageVisibleToPlayer(long stageID, Players player, long cardIDUsed) {
        if(cardIDUsedToRevealStage.contains(cardIDUsed)) {
            LOG.warn(String.format("PlayerID %d attempted to make stage %d visible, but cardID %d has already been used! Rejected request."),player.getPlayerId(),stageID,cardIDUsed);
            return false;
        }

        for(StagePlayAreas s : stages) {
            if(s.getStageID()==stageID) {
                cardIDUsedToRevealStage.add(cardIDUsed);
                HashSet<Players> playerVisibilityList = stageVisibleToPlayersList.get(s);
                if(playerVisibilityList == null) {
                    playerVisibilityList = new HashSet<>();
                    stageVisibleToPlayersList.put(s,playerVisibilityList);
                }
                playerVisibilityList.add(player);
                s.notifyStageAreaChanged();
                LOG.info(String.format("Quest Phase has made stageID %d visible to playerID %d (Name: %s).",stageID,player.getPlayerId(),player.getName()));
                return true;
            }
        }
        LOG.warn(String.format("PlayerID %d attempted to make stage %d visible, but stage was not found. Rejected request!"),player.getPlayerId(),stageID);
        return false;
    }

    public boolean notifyStageAreaChanged(StagePlayAreas stage,StageAreaData fullData, StageAreaData obfsucatedData) {
        //Find out which stages are visible to someone.
        if(!stages.contains(stage)) {
            return false;
        }

        HashSet<Players> visibleList = stageVisibleToPlayersList.get(stage);
        if(visibleList==null) {
            visibleList = new HashSet<>();
        }
        QuestPhaseOutboundService.getService().broadcastStageChanged(visibleList,fullData,obfsucatedData);
        return true;
    }

    /**
     * Called by State Machine when there is a new state.
     * It will not be called if updating the state resulted in the same state.
     */
    public void executeNextAction() {
        switch(stateMachine.getCurrentState()) {
            case QUEST_SPONSOR -> {
                // Attempts sponsor search on the next player
                QuestPhaseOutboundService.getService().broadcastSponsorSearch(playerTurnService.getPlayerTurn().generatePlayerData());
            }
            case QUEST_SETUP -> {
                QuestPhaseOutboundService.getService().broadcastSponsorFound(sponsor.generatePlayerData());
                setupStage();
            }
            case IN_STAGE -> resolveStage(curStageIndex);
            case IN_TEST -> {
                //TODO: Broadcast Test start... maybe just go into participant setup
                testSetup();
            }
            case DRAW_CARD -> dealAdventureCard();
            case REWARDS -> distributeRewards();
            case ENDED -> endPhase();
            case QUEST_JOIN -> QuestPhaseOutboundService.getService().broadcastJoinRequest();
            case PARTICIPANT_SETUP -> participantSetup();
            default -> {
                throw new IllegalQuestPhaseStateException("Unknown state");
            }
        }
    }


}
