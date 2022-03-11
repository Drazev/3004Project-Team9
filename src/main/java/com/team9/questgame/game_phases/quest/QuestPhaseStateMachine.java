package com.team9.questgame.game_phases.quest;

import com.team9.questgame.game_phases.StateMachineI;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class QuestPhaseStateMachine implements StateMachineI<QuestPhaseStatesE> {

    @Getter
    private QuestPhaseStatesE currentState;
    @Getter
    private QuestPhaseStatesE previousState;

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
        return QuestPhaseStatesE.QUEST_SPONSOR;
    }

    public void onGameReset() {
        previousState = null;
        currentState = QuestPhaseStatesE.NOT_STARTED;
        isPhaseStartRequested = false;
    }
}
