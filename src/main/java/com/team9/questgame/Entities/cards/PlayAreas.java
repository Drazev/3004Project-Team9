package com.team9.questgame.Entities.cards;

import com.team9.questgame.game_phases.GamePhaseControllers;

public interface PlayAreas<T extends Cards> extends CardArea<T> {
    int getBattlePoints();
    int getBids();
    void onGamePhaseEnded();
}
