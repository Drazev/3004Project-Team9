package com.team9.questgame.Entities.Effects.CardEffects;

import com.team9.questgame.Entities.Effects.EffectResolverService;
import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Effects.TargetSelector;
import com.team9.questgame.Entities.Effects.TargetSelectors.HighestRankSelector;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.AllCardCodes;
import com.team9.questgame.Entities.cards.CardTypes;

import java.util.ArrayList;
import java.util.HashMap;

public class KingsCallToArms extends Effects {
    @Override
    protected ArrayList<TargetSelector> initTargetSelectors() {
        ArrayList<TargetSelector> selectors = new ArrayList<>();
        selectors.add(new HighestRankSelector());
        return selectors;
    }

    @Override
    protected void onActivated() {
        nextState();
    }

    @Override
    protected void onEffectResolution() {
        HashMap<Players, HashMap<CardTypes,Integer>> discardList = new HashMap<>();
        this.possibleTargerts=this.targetSelectors.get(0).selectTargets(possibleTargerts);

        for(Players p : possibleTargerts) {
            HashMap<CardTypes,HashMap<AllCardCodes,Integer>> cardsOnHand = p.getHand().getNumberOfEachCardCodeBySubType();
            HashMap<CardTypes,Integer> list = new HashMap<>();
            if(cardsOnHand.containsKey(CardTypes.WEAPON) && cardsOnHand.get(CardTypes.WEAPON).size()>0) {
                list.put(CardTypes.WEAPON,1);
                discardList.put(p,list);
            }
            else if(cardsOnHand.containsKey(CardTypes.FOE) && cardsOnHand.get(CardTypes.FOE).size()>0) {
                //Discard up to 2 Foe cards if available
                int numDiscard = cardsOnHand.get(CardTypes.FOE).size()>1 ? 2 : 1;
                list.put(CardTypes.FOE,numDiscard);
                discardList.put(p,list);
            }
        }
        EffectResolverService.getService().forcePlayerDiscards(this,discardList);
        waitForResolutionTrigger();
    }
}
