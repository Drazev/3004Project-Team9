package com.team9.questgame.GamePhases;

import com.team9.questgame.Data.PlayerRewardData;
import com.team9.questgame.Entities.cards.CardArea;

public interface GamePhases extends CardArea {

    PlayerRewardData getRewards();
    void startPhase();


}
