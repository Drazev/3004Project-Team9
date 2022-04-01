package com.team9.questgame.Entities.cards;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.Serializable;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestPlayArea implements PlayAreas<Cards>, Serializable {
    int bids=0;
    int bp=0;
    @Override
    public boolean receiveCard(Cards card) {
        return false;
    }

    @Override
    public void discardCard(Cards card) {

    }

    @Override
    public boolean playCard(Cards card) {
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

    @Override
    public void registerBoostableCard(BoostableCard card) {

    }

    @Override
    public void registerMinBid(TestCards card){

    }

    @Override
    public void registerBattlePointContributor(BattlePointContributor card){

    }
}
