package com.team9.questgame.Entities.Effects.CardEffects;

import com.team9.questgame.Entities.Effects.EffectResolverService;
import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Effects.TargetSelector;
import com.team9.questgame.Entities.Players;

import java.util.ArrayList;
import java.util.HashMap;

public class PoxEffect extends Effects {
    @Override
    protected ArrayList<TargetSelector> initTargetSelectors() {
        return null; //We will not use this because its a trivial case
    }

    @Override
    protected void onActivated() {
        nextState();
    }

    @Override
    protected void onEffectResolution() {
        this.possibleTargerts.remove(activatedBy);
        HashMap<Players,Integer> targetList = new HashMap<>();
        for(Players p : possibleTargerts) {
            targetList.put(p,1);
        }
        EffectResolverService.getService().playerLoosesShieldsHashMap(targetList);
        nextState();
    }
}
