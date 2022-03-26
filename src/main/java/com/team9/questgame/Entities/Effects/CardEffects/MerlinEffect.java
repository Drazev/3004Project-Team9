package com.team9.questgame.Entities.Effects.CardEffects;

import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Effects.TargetSelectionRequestTypes;
import com.team9.questgame.Entities.Effects.TargetSelector;

import java.util.ArrayList;

public class MerlinEffect extends Effects {
    @Override
    protected ArrayList<TargetSelector> initTargetSelectors() {
        return null;
    }

    @Override
    protected void onActivated() {
        waitForTargetSelectionRequest(TargetSelectionRequestTypes.STAGE_TARGET_SELECTION,"Select a stage for Merlin to reveal!");
    }

    @Override
    protected void onEffectResolution() {

    }
}
