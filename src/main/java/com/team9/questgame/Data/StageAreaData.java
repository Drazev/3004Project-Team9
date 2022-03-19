package com.team9.questgame.Data;

import com.team9.questgame.Entities.cards.CardTypes;

import java.util.ArrayList;
import java.util.HashSet;

public record StageAreaData(
        long id,
        int stageNum,
        int bids,
        int battlePoints,
        HashSet<CardTypes> acceptedCardTypes,
        CardData stageCard,
        ArrayList<CardData> activeCards
) {
}
