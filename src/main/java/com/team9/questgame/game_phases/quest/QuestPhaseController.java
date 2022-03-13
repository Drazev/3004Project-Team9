package com.team9.questgame.game_phases.quest;

import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Data.PlayerRewardData;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.*;
import com.team9.questgame.exception.IllegalGameRequest;
import com.team9.questgame.exception.SponsorAlreadyExistsException;
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
public class QuestPhaseController implements GamePhases<QuestCards> {
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
    private ArrayList<Players> players;
    @Getter
    private ArrayList<Players> questingPlayers;
    @Getter
    private PlayerTurnService playerTurnService;
    @Getter
    private PlayerTurnService questTurnService;
    @Getter
    private Players sponsor;
    @Getter
    private int sponsorAttempts;
    @Getter
    private int joinAttempts;
    @Getter
    private int numStages;

    private ArrayList<StagePlayAreas> stages;


    public QuestPhaseController() {
        LOG = LoggerFactory.getLogger(QuestPhaseController.class);
        this.players = new ArrayList<>();
        this.questingPlayers = new ArrayList<>();
        this.stages = new ArrayList<>();
        playerTurnService = new PlayerTurnService(players);
        sponsor = null;
        sponsorAttempts = 0;
        numStages = 0;
        this.outboundService = ApplicationContextHolder.getContext().getBean(QuestPhaseOutboundService.class);

    }

    @Override
    public boolean receiveCard(QuestCards card) {
        if(stateMachine.getCurrentState() != QuestPhaseStatesE.NOT_STARTED){
            throw new IllegalQuestPhaseStateException("Quest can only receive questCard if no quest is currently in progress");
            // TODO: return false when error because this is an internal communication
        }

        questCard = card;

        stateMachine.update();
        return true;
    }

    @Override
    public void discardCard(QuestCards card) {
        // TODO: Discard the card to the discard pile
        //       Problem: the card cannot discard itself
    }

    @Override
    public boolean playCard(QuestCards card) {
        // TODO: calls current stage area's receiveCard function
        return false;
    }

    @Override
    public PlayerRewardData getRewards() {
        return null;
    }


    /**
     * Starts the quest phase
     * Assume that the phase has already been reset (NOT_STARTED state)
     * @param playerTurnService A turn service in which the first turn is the player who drawn the Quest Card
     */
    @Override
    public void startPhase(PlayerTurnService playerTurnService) {
        if (questCard == null) {
            throw new RuntimeException("Cannot start quest phase, questCard is null");
        }

        stateMachine.setPhaseStartRequested(true);
        stateMachine.update();
        if (stateMachine.getCurrentState() == QuestPhaseStatesE.QUEST_SPONSOR) {
            LOG.info("Quest phase started");
            this.playerTurnService = playerTurnService;
            outboundService.broadcastSponsorSearch(playerTurnService.getPlayerTurn().generatePlayerData());
        }
    }


    /**
     * Check client sponsor result
     * @param player the player who made the sponsor request
     * @param found
     */
    public void checkSponsorResult(Players player, boolean found){
        if(stateMachine.getCurrentState() != QuestPhaseStatesE.QUEST_SPONSOR){
            throw new IllegalQuestPhaseStateException("Cannot check sponsor result when not in QUEST_SPONSOR state" );
        } else if (player.getPlayerId() != playerTurnService.getPlayerTurn().getPlayerId()) {
            throw new IllegalGameRequest("Must be current player turn to send sponsor resullt",);
        }

        // Increment sponsorAttempts counter so that the state machine know when we run out of attempts
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
                // TODO: Broadcast to all players to setup
                //      { "sponsor": Players }
            } case ENDED -> {
                endPhase();
            }
        }
    }

    public void checkJoins(){
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
        if(joined){
            questingPlayers.add(player);
            if(joinAttempts == playerTurnService.getPlayers().size()-1){
                questTurnService = new PlayerTurnService(questingPlayers);
            }
        }
        stateMachine.update();
    }


    public void noSponsor(){
        stateMachine.setSponsorFoundRequest(false);
    }

    public void setupStage(){
        StagePlayAreas stage = new StagePlayAreas();
        //System.out.println("num stages="+questCard.getStages()+" setupStage take "+numStages );

        //broadcast
        //TODO:
        numStages++;
        stateMachine.update();
    }

    /**
     * Reset the phase
     */
    @Override
    public void endPhase() {

    }

    /**
     * Reset the game
     */
    @Override
    public void onGameReset() {

    }

}
