package com.team9.questgame.Entities.cards;

public interface PlayAreas<T extends Cards> extends CardArea<T> {
    int getBattlePoints();
    int getBids();
    void onGamePhaseEnded();
    void registerBoostableCard(BoostableCard card);
}
