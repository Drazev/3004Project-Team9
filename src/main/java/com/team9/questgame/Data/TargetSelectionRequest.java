package com.team9.questgame.Data;

import com.team9.questgame.Entities.Effects.TargetSelectionRequestTypes;
import com.team9.questgame.Entities.cards.AllCardCodes;

public record TargetSelectionRequest (
        long requestID,
        long requestPlayerID,
        String notificationMessage,
        AllCardCodes requestCardCode,
        TargetSelectionRequestTypes responseType
){
}
