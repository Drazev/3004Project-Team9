package com.team9.questgame.Data;

import com.team9.questgame.Entities.cards.AllCardCodes;
import com.team9.questgame.Entities.cards.CardTypes;

public record CardData(
        long cardID,
        AllCardCodes cardCode,
        String cardName,
        CardTypes subType,
        String imgSrc,
        int bids,
        int battlePoints,
        String effectDescription,
        boolean hasActiveEffect
) {
}
