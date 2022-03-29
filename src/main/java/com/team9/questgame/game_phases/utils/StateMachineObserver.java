package com.team9.questgame.game_phases.utils;

public interface StateMachineObserver<T extends Enum> {
    void observerStateChanged(T newState);
}
