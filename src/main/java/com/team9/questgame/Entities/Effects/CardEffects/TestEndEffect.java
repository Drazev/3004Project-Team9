package com.team9.questgame.Entities.Effects.CardEffects;

import com.team9.questgame.Entities.Effects.EffectObserver;
import com.team9.questgame.Entities.Effects.EffectResolverService;
import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Effects.TargetSelector;
import com.team9.questgame.Entities.Effects.TargetSelectors.HighestRankSelector;
import com.team9.questgame.Entities.Players;

import java.util.ArrayList;

public class TestEndEffect extends Effects {
    private Players player;
    private int cardsDiscarded;

    public TestEndEffect(Players player, int cardsDiscarded){
        this.player = player;
        this.cardsDiscarded = cardsDiscarded;

    }
    @Override
    protected ArrayList<TargetSelector> initTargetSelectors(){
        return null;
    }

    @Override
    protected void onActivated() {
        nextState();
    }

    @Override
    protected void onEffectResolution(){
        EffectResolverService.getService().forcePlayerDiscards(this, player , cardsDiscarded );
        waitForResolutionTrigger();
    }
}
