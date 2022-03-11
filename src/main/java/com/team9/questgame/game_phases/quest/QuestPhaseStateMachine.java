package com.team9.questgame.game_phases.quest;

import com.team9.questgame.game_phases.StateMachineI;

public class QuestPhaseStateMachine implements StateMachineI<QuestPhaseStatesE> {


    private QuestPhaseStatesE currentState;
    private QuestPhaseStatesE previousState;

    public QuestPhaseStateMachine() {
        previousState = null;
        currentState = QuestPhaseStatesE.NOT_STARTED;
    }

    @Override
    public void update() {

    }
}
