package com.team9.questgame.game_phases.quest;

import com.team9.questgame.exception.IllegalQuestPhaseStateException;
import com.team9.questgame.game_phases.StateMachineI;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class QuestPhaseStateMachine implements StateMachineI<QuestPhaseStatesE> {

    @Getter
    @Setter
    private QuestPhaseStatesE currentState;
    @Getter
    private QuestPhaseStatesE previousState;

    @Autowired
    @Lazy
    private QuestPhaseController controller;

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

    public QuestPhaseStateMachine() {
        previousState = null;
        currentState = QuestPhaseStatesE.NOT_STARTED;
        isPhaseStartRequested = false;
    }

    @Override
    public void update() {
        QuestPhaseStatesE tempState = this.currentState;
        if (isBlockRequested) {
            // BLOCKED can be transitioned to from every stage
            this.currentState = QuestPhaseStatesE.BLOCKED;
        } else {
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
                case PARTICIPANT_SETUP:
                    this.currentState = participantSetupState();
                    break;
                case IN_STAGE:
                    this.currentState = inStageState();
                    break;
                case IN_TEST:
                    this.currentState = inTestState();
//                case STAGE_ONE:
//                    this.currentState = stageOneState();
//                    break;
//                case STAGE_TWO:
//                    this.currentState = stageTwoState();
//                    break;
//                case STAGE_THREE:
//                    this.currentState = stageThreeState();
//                    break;
                case ENDED:
                    this.currentState = endedState();
                    break;
                case BLOCKED:
                    this.currentState = blockStage();
                    break;
                default:
                    throw new IllegalStateException("Unknown state: " + currentState);
            }
        }

        if (tempState != currentState) {
            // Stage changed, update previousState
            this.previousState = tempState;
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
        if (isPhaseStartRequested && controller.getQuestCard() != null) {
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
                return QuestPhaseStatesE.ENDED;
            }
            if(controller.isNextStageTest()){
                return QuestPhaseStatesE.IN_TEST;
            }
            return QuestPhaseStatesE.PARTICIPANT_SETUP;
        }
        return QuestPhaseStatesE.QUEST_JOIN;
    }

    public QuestPhaseStatesE participantSetupState(){
        if(controller.getParticipantSetupResponses() >= controller.getQuestingPlayers().size()){
            if(controller.getQuestingPlayers().size() == 0){
                return QuestPhaseStatesE.ENDED;
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
        if(controller.getCurStageIndex() >= controller.getQuestCard().getStages() || controller.getQuestingPlayers().size() == 0){
            return QuestPhaseStatesE.ENDED;
        }
        return QuestPhaseStatesE.PARTICIPANT_SETUP;
    }

    public QuestPhaseStatesE inTestState(){
        if(!controller.isNextStageTest()){
            if(controller.getCurStageIndex() > controller.getQuestCard().getStages()){
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
}
