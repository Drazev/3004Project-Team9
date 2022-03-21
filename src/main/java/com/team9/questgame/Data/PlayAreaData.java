package com.team9.questgame.Data;

import com.team9.questgame.Entities.cards.CardTypes;

import java.util.ArrayList;
import java.util.HashSet;

public record PlayAreaData(
        PlayAreaDataSources source,
        long id,
        int bids,
        int battlePoints,
        ArrayList<CardData> cardsInPlay
) {
}
