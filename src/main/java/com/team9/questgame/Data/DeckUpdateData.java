package com.team9.questgame.Data;

import com.team9.questgame.Entities.DeckTypes;

import java.util.ArrayList;

public record DeckUpdateData(
        DeckTypes deckType,
        int drawDeckSize,
        ArrayList<CardData> discardPileCards
) {
}
