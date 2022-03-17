package com.team9.questgame.game_phases;

import com.team9.questgame.Data.PlayerRewardData;
import com.team9.questgame.Entities.cards.Cards;
import com.team9.questgame.Entities.cards.StoryDeckCards;
import com.team9.questgame.game_phases.utils.PlayerTurnService;

public interface GamePhaseControllers {

    /**
     * Play area's call this to have playing a card approved.
     * @param card
     * @return
     */
    boolean cardPlayRequest(Cards card);
    void startPhase(PlayerTurnService playerTurnService);
    PlayerRewardData getRewardData();
    StoryDeckCards getPhaseCardCode();
}
