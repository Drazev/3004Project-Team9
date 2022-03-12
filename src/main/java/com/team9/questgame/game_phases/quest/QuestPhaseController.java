package com.team9.questgame.game_phases.quest;

import com.team9.questgame.Data.PlayerRewardData;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.*;
import com.team9.questgame.game_phases.GamePhases;
import com.team9.questgame.game_phases.GeneralGameController;
import com.team9.questgame.exception.IllegalQuestPhaseStateException;
import com.team9.questgame.game_phases.utils.PlayerTurnService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;

@Component
public class QuestPhaseController implements GamePhases {
    Logger LOG;
    @Getter
    @Autowired
    private QuestPhaseStateMachine stateMachine;
    @Getter
    private QuestCards questCard;

    @Autowired
    @Lazy
    private GeneralGameController generalController;

    @Getter
    private ArrayList<Players> players;
    @Getter
    private PlayerTurnService playerTurnService;
    @Getter
    private Players sponsor;

    private StagePlayAreas stage;


    public QuestPhaseController() {
        LOG = LoggerFactory.getLogger(QuestPhaseController.class);
        this.players = new ArrayList<>();
        playerTurnService = new PlayerTurnService(players);
        sponsor = null;
    }

   // @Override
    public boolean receiveCard(QuestCards card) {
        if(stateMachine.getCurrentState() != QuestPhaseStatesE.NOT_STARTED){
            throw new IllegalQuestPhaseStateException("Quest can only receive storycard if no quest is currently in progress");
        }
        if (card.getSubType() == CardTypes.QUEST) {
            questCard = card; // TODO: Remove type casting
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

    @Override
    public void onGameReset() {



    }

    @Override
    public PlayerRewardData getRewards() {
        return null;
    }

    @Override
    public void startPhase() {
        if (questCard == null) {
            throw new RuntimeException("Cannot start quest phase, questCard is null");
        }
        onGameReset();
        stateMachine.setPhaseStartRequested(true);
        stateMachine.update();
        if (stateMachine.getCurrentState() == QuestPhaseStatesE.QUEST_SPONSOR) {
            // broadcast that quest has started
            LOG.info("Quest phase started");
        }
    }
}
