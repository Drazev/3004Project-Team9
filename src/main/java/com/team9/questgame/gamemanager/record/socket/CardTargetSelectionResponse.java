package com.team9.questgame.gamemanager.record.socket;

import com.team9.questgame.Entities.Effects.TargetSelectionRequestTypes;

public record CardTargetSelectionResponse (
        long requestID,
        long requestPlayerID,
        long targetPlayerID,
        long targetCardID
    )
{
}
