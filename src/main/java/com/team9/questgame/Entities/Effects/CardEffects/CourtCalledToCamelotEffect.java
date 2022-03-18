package com.team9.questgame.Entities.Effects.CardEffects;

import com.team9.questgame.Entities.Effects.EffectResolverService;
import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Effects.TargetSelector;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.CardTypes;

import java.util.ArrayList;
import java.util.HashSet;

public class CourtCalledToCamelotEffect extends Effects {
    @Override
    protected ArrayList<TargetSelector> initTargetSelectors() {
        return null; //trivial
    }

    @Override
    protected void onActivated() {
        nextState();
    }

    @Override
    protected void onEffectResolution() {
        HashSet<Players> targetList = new HashSet<Players>(possibleTargerts);
        HashSet<CardTypes> typeList = new HashSet<CardTypes>();
        typeList.add(CardTypes.ALLY);
        EffectResolverService.getService().playerDiscardsAllCardsFromPlay(targetList,typeList);
        nextState();
    }
}
