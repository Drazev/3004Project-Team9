package com.team9.questgame.GamePhases;

import com.team9.questgame.Data.PlayerRewardData;
import com.team9.questgame.Entities.cards.Cards;
import com.team9.questgame.Entities.cards.StoryDeckCards;

public interface GamePhaseControllers {

    /**
     * Play area's call this to have playing a card approved.
     * @param card
     * @return
     */
    boolean cardPlayRequest(Cards card);
    void startPhase();
    PlayerRewardData getRewardData();
    StoryDeckCards getPhaseCardCode();

}
