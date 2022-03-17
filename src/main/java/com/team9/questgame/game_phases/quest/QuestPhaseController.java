package com.team9.questgame.game_phases.quest;

import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Data.PlayerRewardData;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.*;
import com.team9.questgame.exception.IllegalGameRequest;
import com.team9.questgame.exception.SponsorAlreadyExistsException;
import com.team9.questgame.game_phases.GamePhaseControllers;
import com.team9.questgame.game_phases.GamePhases;
import com.team9.questgame.game_phases.GeneralGameController;
import com.team9.questgame.exception.IllegalQuestPhaseStateException;
import com.team9.questgame.game_phases.utils.PlayerTurnService;
import com.team9.questgame.gamemanager.service.QuestPhaseOutboundService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class QuestPhaseController implements GamePhaseControllers {
    Logger LOG;
    @Getter
    @Autowired
    private QuestPhaseStateMachine stateMachine;
    @Getter
    private QuestCards questCard;

    @Autowired
    @Lazy
    private GeneralGameController generalController;

    @Autowired
    private QuestPhaseOutboundService outboundService;
    @Getter
    private ArrayList<Players> questingPlayers;
    @Getter
    private PlayerTurnService playerTurnService;
    @Getter
    private PlayerTurnService questTurnService;
    @Getter
    private ArrayList<StagePlayAreas> stages;
    private StagePlayAreas curStage;
    @Getter
    private Players sponsor;
    @Getter
    private int sponsorAttempts;
    @Getter
    private int joinAttempts;

    public QuestPhaseController() {
        LOG = LoggerFactory.getLogger(QuestPhaseController.class);
        this.questingPlayers = new ArrayList<>();
        this.stages = new ArrayList<>();
        playerTurnService = null;
        sponsor = null;
        sponsorAttempts = 0;
        this.outboundService = ApplicationContextHolder.getContext().getBean(QuestPhaseOutboundService.class);

    }

    /**
     * Receive the QuestCard from the GeneralGameController
     * @param card
     * @return
     */
    public boolean receiveCard(QuestCards card) {
        if(stateMachine.getCurrentState() != QuestPhaseStatesE.NOT_STARTED){
            throw new IllegalQuestPhaseStateException("Quest can only receive questCard if no quest is currently in progress");
            // TODO: return false when error because this is an internal communication
        }

        questCard = card;

        stateMachine.update();
        return true;
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
        }

        stateMachine.setPhaseStartRequested(true);
        stateMachine.update();
        if (stateMachine.getCurrentState() == QuestPhaseStatesE.QUEST_SPONSOR) {
            LOG.info("Quest phase started");
            this.playerTurnService = playerTurnService;
            registerPlayerPlayAreas(questCard);
            outboundService.broadcastSponsorSearch(playerTurnService.getPlayerTurn().generatePlayerData());
        }
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
        }

        // Increment sponsorAttempts counter so that the state machine knows when we run out of attempts
        this.sponsorAttempts++;

        if(found){
            if(this.sponsor != null){
                throw new SponsorAlreadyExistsException(player);
            }
            this.sponsor = player;
        }
        
        stateMachine.update();
        switch (stateMachine.getCurrentState()) {
            case QUEST_SPONSOR -> {
                // Attempts sponsor search on the next player
                playerTurnService.nextPlayer();
                outboundService.broadcastSponsorSearch(playerTurnService.getPlayerTurn().generatePlayerData());
            } case QUEST_SETUP -> {
                outboundService.broadcastSponsorFound(sponsor.generatePlayerData());
                this.setupStage();
            } case ENDED -> {
                endPhase();
            } default -> {
                throw new IllegalQuestPhaseStateException("Unknown state");
            }
        }
    }

    /**
     * Called by checkSponsorResult() after a sponsor is found and the quest is set up for the first time
     */
    private void setupStage(){
        curStage = new StagePlayAreas(questCard, stages.size());
        sponsor.getPlayArea().onPlayAreaChanged(curStage);
        //System.out.println("num stages="+questCard.getStages()+" setupStage take "+numStages );
        outboundService.broadcastSponsorSetup(sponsor.generatePlayerData());

        stateMachine.update();
    }

    /*
    * Called by sponsorSetupStage() after a stage is reported as being setup
    * */
    public void stageSetupComplete(boolean complete){
        if(stateMachine.getCurrentState() != QuestPhaseStatesE.QUEST_SETUP){
            throw new IllegalQuestPhaseStateException("Cannot set up a stage when not in QUEST_SETUP phase");
        }
        if(!complete | curStage.getBattlePoints() == 0){
            throw new RuntimeException("temp exception sponsor setup was not completed");
        }
        stages.add(curStage);
        stateMachine.update();
        switch (stateMachine.getCurrentState()) {
            case QUEST_SETUP -> {
                outboundService.broadcastSponsorFound(sponsor.generatePlayerData());
                this.setupStage();
            } case QUEST_JOIN -> {
                this.checkJoins();
            } default -> {
                throw new IllegalQuestPhaseStateException("Unknown state");
            }
        }

    }

    /**
     * Called by stageSetup
     */
    private void checkJoins(){
        if(joinAttempts == 0){
            playerTurnService.setPlayerTurn(sponsor);
            playerTurnService.nextPlayer();
        }
        Players player = playerTurnService.getPlayerTurn();
        outboundService.broadcastJoinRequest(player.generatePlayerData());
        playerTurnService.nextPlayer();
        this.joinAttempts++;

    }

    public void checkJoinResult(Players player, boolean joined){
        if (stateMachine.getCurrentState() != QuestPhaseStatesE.QUEST_JOIN) {
            throw new IllegalQuestPhaseStateException("A player can only join when the quest is in QUEST_JOIN state");
        }

        if(joined){
            questingPlayers.add(player);
            if(joinAttempts == playerTurnService.getPlayers().size()-1){
                questTurnService = new PlayerTurnService(questingPlayers);
            }
        }

        stateMachine.update();
        switch (stateMachine.getCurrentState()) {
            case QUEST_JOIN -> {
                this.checkJoins();
            } case STAGE_ONE -> {
            } case ENDED -> {
            } default -> {
                throw new IllegalQuestPhaseStateException("Unknown state");
            }
        }
    }

    /**
     * Reset the phase
     */
    public void endPhase() {
        if (stateMachine.getCurrentState() != QuestPhaseStatesE.ENDED) {
            throw new IllegalQuestPhaseStateException("Cannot end phase when it's not in ENDED state");
        }
        this.questCard.discardCard();
        this.questCard = null;
        stateMachine.setPhaseReset(true);
        stateMachine.update();
    }

    /**
     * Reset the game
     */
    public void onGameReset() {

    }

    @Override
    public boolean cardPlayRequest(Cards card){return false;}

    private void registerPlayerPlayAreas(QuestCards card){
        for(Players player : playerTurnService.getPlayers()){
            player.getPlayArea().registerGamePhase(this);
            player.getPlayArea().onQuestStarted(card);
        }
    }

    @Override
    public PlayerRewardData getRewardData(){
        return null;
    }

    @Override
    public StoryDeckCards getPhaseCardCode(){
        return null;
    }

}
