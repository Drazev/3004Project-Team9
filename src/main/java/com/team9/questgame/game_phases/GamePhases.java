package com.team9.questgame.game_phases;

import com.team9.questgame.Data.PlayerRewardData;
import com.team9.questgame.Entities.cards.CardArea;
import com.team9.questgame.Entities.cards.Cards;
import com.team9.questgame.Entities.cards.StoryCards;
import com.team9.questgame.game_phases.utils.PlayerTurnService;

public interface GamePhases<T extends StoryCards,U extends Enum> {
    U getCurrState();
    void startPhase(PlayerTurnService playerTurnService);
    void endPhase();
    T getCard();
}
