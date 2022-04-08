package com.team9.questgame.Entities.Effects.CardEffects;

import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Effects.TargetSelectionRequestTypes;
import com.team9.questgame.Entities.Effects.TargetSelector;
import com.team9.questgame.gamemanager.service.InboundService;

import java.util.ArrayList;

public class MordredEffect extends Effects {
    @Override
    protected ArrayList<TargetSelector> initTargetSelectors() {
        return null;
    }

    @Override
    protected void onActivated() {
        waitForTargetSelectionRequest(TargetSelectionRequestTypes.CARD_TARGET_SELECTION,"Select an ALLY card in play for mordred to destroy.");
    }

    @Override
    protected void onEffectResolution() {
    }

    @Override
    protected void onResolved() {
        // Discard the card before resolving the effect
        super.onResolved();
        InboundService.getService().trigger();
    }
}
