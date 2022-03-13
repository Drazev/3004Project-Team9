package com.team9.questgame.Entities.Effects.EffectOutcomes;

import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Entities.Effects.EffectOutcomesResolver;
import com.team9.questgame.Entities.Effects.EffectResolverService;
import com.team9.questgame.Entities.Players;

import java.util.HashSet;

public class AllPlayersDrawAdventureCards implements EffectOutcomesResolver {
    @Override
    public boolean applyEffect(HashSet<Players> targets) {
        EffectResolverService effectResolver= ApplicationContextHolder.getContext().getBean(EffectResolverService .class);
        for(Players p : targets) {

        }
        return false;
    }

    @Override
    public void getSuccessMessage() {

    }

    @Override
    public void getFailureMessage() {

    }

    @Override
    public boolean isNegativeEffect() {
        return false;
    }
}
