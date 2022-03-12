package com.team9.questgame.game_phases.quest;

import com.team9.questgame.game_phases.StateMachineI;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class QuestPhaseStateMachine implements StateMachineI<QuestPhaseStatesE> {

    @Getter
    @Setter
    private QuestPhaseStatesE currentState;
    @Getter
    private QuestPhaseStatesE previousState;

    @Lazy
    private QuestPhaseController controller;

    @Setter
    private boolean isPhaseStartRequested;

    public QuestPhaseStateMachine() {
        previousState = null;
        currentState = QuestPhaseStatesE.NOT_STARTED;
        isPhaseStartRequested = false;
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
        if (isPhaseStartRequested) {
            return QuestPhaseStatesE.QUEST_SPONSOR;
        } else {
            return QuestPhaseStatesE.NOT_STARTED;
        }

    }

    private QuestPhaseStatesE questSponsorState() {
        if(controller.getSponsor() != null){
            return QuestPhaseStatesE.QUEST_SETUP;
        }

        return QuestPhaseStatesE.QUEST_SPONSOR;
    }

    public void onGameReset() {
        previousState = null;
        currentState = QuestPhaseStatesE.NOT_STARTED;
        isPhaseStartRequested = false;
    }

    public QuestPhaseStatesE questSetupState(){

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
