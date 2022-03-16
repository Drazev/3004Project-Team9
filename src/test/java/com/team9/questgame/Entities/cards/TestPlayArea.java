package com.team9.questgame.Entities.cards;

import java.io.Serializable;

public class TestPlayArea implements PlayAreas<AdventureCards>, Serializable {
    int bids=0;
    int bp=0;
    @Override
    public boolean receiveCard(AdventureCards card) {
        return true;
    }

    @Override
    public void discardCard(AdventureCards card) {

    }

    @Override
    public boolean playCard(AdventureCards card) {
        return false;
    }

    @Override
    public void onGameReset() {

    }

    @Override
    public int getBattlePoints() {
        return 0;
    }

    @Override
    public int getBids() {
        return 0;
    }

    @Override
    public void onGamePhaseEnded() {

    }
}
