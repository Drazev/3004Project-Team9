package com.team9.questgame.Entities.cards;

import com.team9.questgame.GamePhases.GamePhaseControllers;

public interface PlayAreas<T extends Cards> extends CardArea<T> {
    int getBattlePoints();
    int getBids();
    void registerGamePhase(GamePhaseControllers activePhase);
    void onGamePhaseEnded();
    boolean registerActiveEffect(AdventureCards card);
    void registerCardBoostDependency(AllCardCodes triggerCardCode, BoostableCard card);
    void registerBidContributor(BidContributor card);
    void registerBattlePointContributor(BattlePointContributor card);
    void removeCardBoostDependency(AllCardCodes triggerCardCode,BoostableCard card);
    void removeBidContributor(BidContributor card);
    void removeBattlePointContributor(BattlePointContributor card);
    void removeActiveEffect(AdventureCards card);
}
