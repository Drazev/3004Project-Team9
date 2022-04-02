package com.team9.questgame.game_phases.quest;

import com.team9.questgame.exception.IllegalQuestPhaseStateException;
import com.team9.questgame.game_phases.GeneralStateE;
import com.team9.questgame.game_phases.GeneralStateMachine;
import com.team9.questgame.game_phases.StateMachineI;
import com.team9.questgame.game_phases.utils.StateMachineObserver;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

public class QuestPhaseStateMachine implements StateMachineI<QuestPhaseStatesE>, StateMachineObserver<GeneralStateE> {
    Logger LOG;
    @Getter
    @Setter
    private QuestPhaseStatesE currentState;
    @Getter
    private QuestPhaseStatesE previousState;

    private final QuestPhaseController controller;

    private GeneralStateE generalGameState;

    @Setter
    private boolean isPhaseStartRequested;

    @Setter
    private boolean isPhaseReset;

    /**
     * Request to block this state machine
     */
    @Setter
    private boolean isBlockRequested;

    /**
     * Request to unblock this state machine
     */
    @Setter
    private boolean isUnblockRequested;

    public QuestPhaseStateMachine(QuestPhaseController phase) {
        LOG = LoggerFactory.getLogger(QuestPhaseController.class);
        previousState = null;
        this.controller = phase;
        currentState = QuestPhaseStatesE.NOT_STARTED;
        isPhaseStartRequested = false;
        generalGameState = GeneralStateMachine.getService().getCurrentState();
    }

    @Override
    public void update() {
        QuestPhaseStatesE tempState = this.currentState;
        switch (currentState) {
            case NOT_STARTED:
                this.currentState = notStartedState();
                break;
            case QUEST_SPONSOR:
                this.currentState = questSponsorState();
                break;
            case QUEST_SETUP:
                this.currentState = questSetupState();
                break;
            case QUEST_JOIN:
                this.currentState = questJoinState();
                break;
            case DRAW_CARD:
                this.currentState = drawCardState();
                break;
            case PARTICIPANT_SETUP:
                this.currentState = participantSetupState();
                break;
            case IN_STAGE:
                this.currentState = inStageState();
                break;
            case IN_TEST:
                this.currentState = inTestState();
                break;
            case REWARDS:
                this.currentState = QuestPhaseStatesE.ENDED;
                break;
            case ENDED:
                this.currentState = endedState();
                break;
            case BLOCKED:
                this.currentState = blockStage();
                break;
            default:
                throw new IllegalStateException("Unknown state: " + currentState);
        }


        if (tempState != currentState || currentState == QuestPhaseStatesE.QUEST_SPONSOR) {
            // Stage changed, update previousState
            LOG.info(String.format("Moved from state %s to state %s", previousState, currentState));
            this.previousState = tempState;
            controller.executeNextAction();
        }

        resetAllRequest();
    }

    private QuestPhaseStatesE blockStage() {
        QuestPhaseStatesE nextState;
        if (isUnblockRequested) {
            // Return to whatever stage is before blocked

            if (isPhaseReset) {
                nextState = QuestPhaseStatesE.NOT_STARTED;
            }else{
                nextState = this.previousState;
            }
        } else {
            nextState = QuestPhaseStatesE.BLOCKED;
        }

        return nextState;
    }

    private QuestPhaseStatesE notStartedState() {
        QuestPhaseStatesE nextState;
        setPhaseReset(false);
        if (isPhaseStartRequested && controller.getCard() != null) {
            nextState = QuestPhaseStatesE.QUEST_SPONSOR;
        } else {
            nextState = QuestPhaseStatesE.NOT_STARTED;
        }
        return nextState;
    }

    private QuestPhaseStatesE questSponsorState() {
        //System.out.println("in sponsor state" + controller.getSponsor().getName());
        if (isBlockRequested) {
            return QuestPhaseStatesE.BLOCKED;
        }
        else if (controller.getSponsor() != null) {
            //System.out.println("got sponsor");
            return QuestPhaseStatesE.QUEST_SETUP;
        } else if (controller.getSponsorAttempts() >= controller.getPlayerTurnService().getPlayers().size()) {
            return QuestPhaseStatesE.ENDED;
        }

        return QuestPhaseStatesE.QUEST_SPONSOR;
    }

    public void onGameReset() {
        previousState = null;
        currentState = QuestPhaseStatesE.NOT_STARTED;
    }

    public QuestPhaseStatesE questSetupState() {
        //if (controller.getStages().size() >= controller.getQuestCard().getStages()) {
        //new if all stages are being created simultaneously
        if(controller.isStagesAreValid()){
            //System.out.println("quest setup state returning join state");
            return QuestPhaseStatesE.QUEST_JOIN;
        }
        return QuestPhaseStatesE.QUEST_SETUP;
    }

    public QuestPhaseStatesE questJoinState() {
        if (controller.getJoinAttempts() >= controller.getPlayerTurnService().getPlayers().size() - 1) {
            if (controller.getQuestingPlayers().size() == 0) {
                return QuestPhaseStatesE.REWARDS;
            }
            if(controller.isNextStageTest()){
                return QuestPhaseStatesE.IN_TEST;
            }
            return QuestPhaseStatesE.DRAW_CARD;
        }
        return QuestPhaseStatesE.QUEST_JOIN;
    }

    public QuestPhaseStatesE drawCardState() {
        if(controller.checkForTest()){
            return QuestPhaseStatesE.IN_TEST;
        }
        return QuestPhaseStatesE.PARTICIPANT_SETUP; //We go to Participant setup. This will be blocked if drawing p
    }

    public QuestPhaseStatesE participantSetupState(){
        if(controller.getParticipantSetupResponses() >= controller.getQuestingPlayers().size()){
            if(controller.getQuestingPlayers().size() == 0){
                return QuestPhaseStatesE.REWARDS;
            }
            return QuestPhaseStatesE.IN_STAGE;
//            switch(controller.getCurStageIndex()){
//                  case 0 -> {
//                    return QuestPhaseStatesE.STAGE_ONE;
//                } case 1 ->{
//                    return QuestPhaseStatesE.STAGE_TWO;
//                } case 2 -> {
//                    return QuestPhaseStatesE.STAGE_THREE;
//                } default -> {
//                    throw new IllegalQuestPhaseStateException("CurStageIndex is out of bounds");
//                }
//            }
        }
        return QuestPhaseStatesE.PARTICIPANT_SETUP;
    }

    public QuestPhaseStatesE inStageState() {
        if(controller.getCurStageIndex() >= controller.getCard().getStages() || controller.getQuestingPlayers().size() == 0){
            return QuestPhaseStatesE.REWARDS;
        }
        return QuestPhaseStatesE.DRAW_CARD;
    }

    public QuestPhaseStatesE inTestState(){
        if(!controller.isNextStageTest()){
            if(controller.getCurStageIndex() >= controller.getCard().getStages()){
                return QuestPhaseStatesE.ENDED;
            }else{
                return QuestPhaseStatesE.PARTICIPANT_SETUP;
            }
        }
        return QuestPhaseStatesE.IN_TEST;
    }

    public QuestPhaseStatesE endedState() {
        if (isPhaseReset) {
            // Change to not started when the controller receive a QuestCard
            return QuestPhaseStatesE.NOT_STARTED;
        }
        return QuestPhaseStatesE.ENDED;
    }

    public boolean isQuestStarted() {
        return this.currentState != QuestPhaseStatesE.NOT_STARTED;
    }

    public boolean isInQuest() {
        return this.currentState != QuestPhaseStatesE.NOT_STARTED && this.currentState != QuestPhaseStatesE.ENDED;
    }

    public boolean isBlocked() {
        return this.currentState == QuestPhaseStatesE.BLOCKED;
    }

    private void resetAllRequest() {
        setPhaseStartRequested(false);
        //setPhaseReset(false);
        setBlockRequested(false);
        setUnblockRequested(false);
    }

    @Override
    public void observerStateChanged(GeneralStateE newState) {
        if(newState==GeneralStateE.PLAYER_HAND_OVERSIZE) {
            this.previousState = this.currentState;
            this.currentState = QuestPhaseStatesE.BLOCKED;
            LOG.info(String.format("Moved from state %s to state %s", previousState, currentState));
        }
        else if(this.currentState==QuestPhaseStatesE.BLOCKED) {
            this.currentState = this.previousState;
            this.previousState = QuestPhaseStatesE.BLOCKED;
            update();
        }
    }
}
