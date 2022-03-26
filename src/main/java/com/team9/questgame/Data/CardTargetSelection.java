package com.team9.questgame.Data;

import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.AllCardCodes;

public record CardTargetSelection(
        long requestID,
        long requestPlayerID,
        AllCardCodes requestCardCode,
        long targetPlayerID,
        long targetCardID
) {

}
