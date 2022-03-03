package com.team9.questgame.Data;

import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.StoryCards;

import java.util.HashMap;

public record PlayerRewardData(StoryCards sourceCard,
                               HashMap<Players, Integer> shieldRewards,
                               HashMap<Players, Integer> cardsToDraw) {

}
