package com.team9.questgame.Entities.Effects.CardEffects;

import com.team9.questgame.Entities.Effects.EffectResolverService;
import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Effects.TargetSelector;
import com.team9.questgame.Entities.Effects.TargetSelectors.LowestRankSelector;
import com.team9.questgame.Entities.Players;

import java.util.ArrayList;
import java.util.HashMap;

public class QueensFavorEffect extends Effects {
    @Override
    protected ArrayList<TargetSelector> initTargetSelectors() {
        ArrayList<TargetSelector> ts = new ArrayList<>();
        ts.add(new LowestRankSelector());
        return ts;
    }

    @Override
    protected void onActivated() {
        nextState();
    }

    @Override
    protected void onEffectResolution() {
        this.possibleTargerts=targetSelectors.get(0).selectTargets(this.possibleTargerts);
        HashMap<Players,Integer> awardList = new HashMap<>();
        for(Players p : possibleTargerts) {
            awardList.put(p,2);
        }
        EffectResolverService.getService().drawAdventureCards(awardList);
        nextState();
    }
}
