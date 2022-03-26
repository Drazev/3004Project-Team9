package com.team9.questgame.gamemanager.record.socket;

import com.team9.questgame.Entities.Effects.TargetSelectionRequestTypes;

public record StageTargetSelectionResponse(
        long requestID,
        long requestPlayerID,
        long targetStageID
) {
}
