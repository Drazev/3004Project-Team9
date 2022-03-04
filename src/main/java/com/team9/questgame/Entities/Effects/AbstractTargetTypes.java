package com.team9.questgame.Entities.Effects;

import com.team9.questgame.Entities.cards.TargetableCardCodes;

public enum AbstractTargetTypes implements TargetableCardCodes<AbstractTargetTypes> {
    ALL_ALLIES,
    PLAYER_CURRENT,
    PLAYER_DRAWING_CARD,
    PLAYER_NEXT_TO_COMPLETE_QUEST,
    PLAYER_LOWEST_RANKING,
    PLAYER_HIGHEST_RANKING
}
