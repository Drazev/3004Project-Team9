package com.team9.questgame.game_phases.quest;

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
    private boolean sponsorFound;



    public QuestPhaseStateMachine() {
        previousState = null;
        currentState = QuestPhaseStatesE.NOT_STARTED;
        isPhaseStartRequested = false;
        sponsorFound = false;
    }

    @Override
    public void update() {
        previousState = currentState;
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
            case STAGE_ONE:
                this.currentState = stageOneState();
                break;
            case STAGE_TWO:
                this.currentState = stageTwoState();
                break;
            case STAGE_THREE:
                this.currentState = stageThreeState();
                break;
            case ENDED:
                this.currentState = endedState();
                break;
            default:
                throw new IllegalStateException("Unknown state: " + currentState);

        }

    }

    private QuestPhaseStatesE notStartedState() {
        QuestPhaseStatesE nextState;
        if (isPhaseStartRequested && isPhaseStartRequested) {
            nextState = QuestPhaseStatesE.QUEST_SPONSOR;
        } else {
            nextState = QuestPhaseStatesE.NOT_STARTED;
        }

        isPhaseStartRequested = false;
        return nextState;
    }

    private QuestPhaseStatesE questSponsorState() {
        if(controller.getSponsor() != null){
            return QuestPhaseStatesE.QUEST_SETUP;
        }//TODO: stopped here, questPhaseInboundService provides result of sponsor search, use that here
        else if(controller.getSponsorAttempts() >= controller.getPlayerTurnService().getPlayers().size()){
            return QuestPhaseStatesE.ENDED;
        }
        controller.checkSponsor();
        return QuestPhaseStatesE.QUEST_SPONSOR;
    }

    public void onGameReset() {
        previousState = null;
        currentState = QuestPhaseStatesE.NOT_STARTED;
        isPhaseStartRequested = false;
    }

    public QuestPhaseStatesE questSetupState(){
        if(controller.getNumStages() >= controller.getQuestCard().getStages()){
            return QuestPhaseStatesE.QUEST_JOIN;
        }
        controller.setupStage();
        return QuestPhaseStatesE.QUEST_SETUP;
    }

    public QuestPhaseStatesE questJoinState(){
        if(controller.getJoinAttempts() >= controller.getPlayerTurnService().getPlayers().size()-1){
            if(controller.getQuestingPlayers().size() == 0){
                return QuestPhaseStatesE.ENDED;
            }
            return QuestPhaseStatesE.STAGE_ONE;
        }
        controller.checkJoins();
        return QuestPhaseStatesE.QUEST_JOIN;
    }

    public QuestPhaseStatesE stageOneState() {
        return currentState;
    }

    public QuestPhaseStatesE stageTwoState() {
        if(controller.getQuestCard().getStages() < 2){
            return QuestPhaseStatesE.ENDED;
        }

        return QuestPhaseStatesE.STAGE_TWO;
    }

    public QuestPhaseStatesE stageThreeState() {
        if(controller.getQuestCard().getStages() < 3){
            return QuestPhaseStatesE.ENDED;
        }

        return QuestPhaseStatesE.STAGE_THREE;
    }

    public QuestPhaseStatesE endedState(){
        return QuestPhaseStatesE.ENDED;
    }
}
