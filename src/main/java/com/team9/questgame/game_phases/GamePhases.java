package com.team9.questgame.game_phases;

import com.team9.questgame.Data.PlayerRewardData;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.CardArea;

public interface GamePhases extends CardArea {

    PlayerRewardData getRewards();
    void startPhase();
}
