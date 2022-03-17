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
    private boolean sponsorFoundRequest;

    public QuestPhaseStateMachine() {
        previousState = null;
        currentState = QuestPhaseStatesE.NOT_STARTED;
        isPhaseStartRequested = false;
        sponsorFoundRequest = false;
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
                //System.out.println(this.currentState +" " +controller.getJoinAttempts());
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
        resetAllRequest();
    }

    private QuestPhaseStatesE notStartedState() {
        QuestPhaseStatesE nextState;
        if (isPhaseStartRequested && controller.getQuestCard() != null) {
            nextState = QuestPhaseStatesE.QUEST_SPONSOR;
        } else {
            nextState = QuestPhaseStatesE.NOT_STARTED;
        }
        return nextState;
    }

    private QuestPhaseStatesE questSponsorState() {
        //System.out.println("in sponsor state" + controller.getSponsor().getName());
        if (controller.getSponsor() != null) {
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
        if (controller.getStages().size() >= controller.getQuestCard().getStages()) {
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
            return QuestPhaseStatesE.STAGE_ONE;
        }
        return QuestPhaseStatesE.QUEST_JOIN;
    }

    public QuestPhaseStatesE stageOneState() {
        return currentState;
    }

    public QuestPhaseStatesE stageTwoState() {
        if (controller.getQuestCard().getStages() < 2) {
            return QuestPhaseStatesE.ENDED;
        }

        return QuestPhaseStatesE.STAGE_TWO;
    }

    public QuestPhaseStatesE stageThreeState() {
        if (controller.getQuestCard().getStages() < 3) {
            return QuestPhaseStatesE.ENDED;
        }

        return QuestPhaseStatesE.STAGE_THREE;
    }

    public QuestPhaseStatesE endedState() {
        return QuestPhaseStatesE.ENDED;
    }

    public boolean isQuestStarted() {
        return this.currentState != QuestPhaseStatesE.NOT_STARTED;
    }

    private void resetAllRequest() {
        setPhaseStartRequested(false);
        setSponsorFoundRequest(false);
    }
}
