package com.team9.questgame.Entities.Effects.CardEffects;

import com.team9.questgame.Entities.Effects.EffectResolverService;
import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Effects.TargetSelector;
import com.team9.questgame.Entities.Effects.TargetSelectors.HighestRankSelector;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.AdventureCards;
import com.team9.questgame.Entities.cards.AdventureDeckCards;
import com.team9.questgame.Entities.cards.AllCardCodes;
import com.team9.questgame.Entities.cards.CardTypes;
import com.team9.questgame.gamemanager.record.socket.NotificationOutbound;
import com.team9.questgame.gamemanager.service.NotificationOutboundService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        HashMap<Players,String> outboundMessages = new HashMap<>();
        this.possibleTargerts=this.targetSelectors.get(0)  .selectTargets(possibleTargerts);
        for(Players p : possibleTargerts) {
            HashMap<CardTypes,HashMap<AllCardCodes<AdventureDeckCards>,Integer>> cardsOnHand = p.getHand().getNumberOfEachCardCodeBySubType();
            HashMap<CardTypes,Integer> list = new HashMap<>();
            if(cardsOnHand.containsKey(CardTypes.WEAPON) && cardsOnHand.get(CardTypes.WEAPON).size()>0) {
                list.put(CardTypes.WEAPON,1);
                discardList.put(p,list);
                outboundMessages.put(p,String.format("The King has called upon you to contribute to the war effort. \n Your must discard %d %s cards!",1,CardTypes.WEAPON));
            }
            else if(cardsOnHand.containsKey(CardTypes.FOE) && cardsOnHand.get(CardTypes.FOE).size()>0) {
                //Discard up to 2 Foe cards if available
                int numDiscard = cardsOnHand.get(CardTypes.FOE).size()>1 ? 2 : 1;
                list.put(CardTypes.FOE,numDiscard);
                outboundMessages.put(p,String.format("The King has called upon you to contribute to the war effort. \n Your must discard %d %s cards!",2,CardTypes.FOE));
                discardList.put(p,list);
            }
        }
        for(Map.Entry<Players,String> e : outboundMessages.entrySet()) {
            NotificationOutbound toAffected = new NotificationOutbound(source.getCardName(),e.getValue(),source.getCard().getImgSrc(),null);
            NotificationOutboundService.getService().sendBadNotification(e.getKey(),toAffected,null);
        }
        EffectResolverService.getService().forcePlayerDiscards(this,discardList);
        waitForResolutionTrigger();
    }
}
