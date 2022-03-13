package com.team9.questgame.game_phases.quest;

import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Data.PlayerRewardData;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.*;
import com.team9.questgame.exception.SponsorAlreadyExistsException;
import com.team9.questgame.game_phases.GamePhases;
import com.team9.questgame.game_phases.GeneralGameController;
import com.team9.questgame.exception.IllegalQuestPhaseStateException;
import com.team9.questgame.Entities.cards.CardTypes;
import com.team9.questgame.game_phases.utils.PlayerTurnService;
import com.team9.questgame.gamemanager.service.OutboundService;
import com.team9.questgame.gamemanager.service.QuestPhaseOutboundService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;

@Component
public class QuestPhaseController implements GamePhases<Cards> {
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
    public boolean receiveCard(Cards card) {
        if(stateMachine.getCurrentState() != QuestPhaseStatesE.NOT_STARTED){
            throw new IllegalQuestPhaseStateException("Quest can only receive questCard if no quest is currently in progress");
        }
        if (card.getSubType() == CardTypes.QUEST) {
            questCard =(QuestCards) card; //TODO: remove typeCast
            return true;
        }
        return false;
    }



    @Override
    public void discardCard(Cards card) {

    }

    @Override
    public boolean playCard(Cards card) {
        return false;
    }

    /**
     * Reset the game
     */
    @Override
    public void onGameReset() {

    }

    @Override
    public PlayerRewardData getRewards() {
        return null;
    }
    /**
     * Reset the phase
     */
    @Override
    public void onPhaseReset() {

    }

    @Override
    public void startPhase(PlayerTurnService playerTurnService) {
        if (questCard == null) {
            throw new RuntimeException("Cannot start quest phase, questCard is null");
        }
        onPhaseReset();
        stateMachine.setPhaseStartRequested(true);

        stateMachine.update();
        if (stateMachine.getCurrentState() == QuestPhaseStatesE.QUEST_SPONSOR) {
            // TODO: broadcast that quest has started
            //       start sponsorQuest() /topic/quest/sponsor
            //           { id: long, name: string }

            LOG.info("Quest phase started");
            this.playerTurnService = playerTurnService;
            stateMachine.update();
        }
    }



    public void checkSponsor(){
//        for(Players player : players){
//            outboundService.broadcastSponsorSearch(player.generatePlayerData());
//        }
          //outboundService.broadcastSponsorSearch();
        Players player = playerTurnService.getPlayerTurn();
        outboundService.broadcastSponsorSearch(player.generatePlayerData());
        playerTurnService.nextPlayer();
        sponsorAttempts++;
    }

    public void checkSponsorResult(Players player, boolean found){
        if(stateMachine.getCurrentState() != (QuestPhaseStatesE.QUEST_SPONSOR)){
            throw new IllegalQuestPhaseStateException("Cannot check sponsor result when not in QUEST_SPONSOR state" );
        }
        if(found){
            if(this.sponsor != null){
                throw new SponsorAlreadyExistsException(player);

            }
            System.out.println("checking Sponsor Result");
            this.sponsor = player;
        }
        
        stateMachine.update();
    }
    public void checkJoins(){
        if(joinAttempts == 0){
            playerTurnService.setPlayerTurn(sponsor);
            playerTurnService.nextPlayer();
        }
        Players player = playerTurnService.getPlayerTurn();
        outboundService.broadcastJoinRequest(player.generatePlayerData());
        playerTurnService.nextPlayer();
        joinAttempts++;

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
        stateMachine.setSponsorFound(false);
    }

    public void setupStage(){
        //StagePlayAreas stage = new StagePlayAreas();
        System.out.println("num stages="+questCard.getStages()+" setupStage take "+numStages );

        //broadcast
        //TODO:
        numStages++;
        stateMachine.update();
    }



}
