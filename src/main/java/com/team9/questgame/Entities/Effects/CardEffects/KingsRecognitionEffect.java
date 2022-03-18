package com.team9.questgame.Entities.Effects.CardEffects;

import com.team9.questgame.Entities.Effects.EffectResolverService;
import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Effects.TargetSelector;
import com.team9.questgame.Entities.Players;

import java.util.ArrayList;
import java.util.HashMap;

public class KingsRecognitionEffect extends Effects {
    @Override
    protected ArrayList<TargetSelector> initTargetSelectors() {
        return null; //trivial
    }

    @Override
    protected void onActivated() {
        waitForTrigger();
    }

    @Override
    protected void onEffectResolution() {
        HashMap<Players,Integer> awardList = new HashMap<Players, Integer>();
        for(Players p : possibleTargerts) {
            awardList.put(p,2);
        }
        EffectResolverService.getService().playerAwardedShields(awardList);
        nextState();
    }
}
