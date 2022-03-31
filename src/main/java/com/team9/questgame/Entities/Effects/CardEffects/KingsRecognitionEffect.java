package com.team9.questgame.Entities.Effects.CardEffects;

import com.team9.questgame.Entities.Effects.EffectResolverService;
import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Effects.TargetSelector;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.gamemanager.record.socket.NotificationOutbound;
import com.team9.questgame.gamemanager.service.NotificationOutboundService;

import java.util.ArrayList;
import java.util.HashMap;

public class KingsRecognitionEffect extends Effects {
    @Override
    protected ArrayList<TargetSelector> initTargetSelectors() {
        return null; //trivial
    }

    @Override
    protected void onActivated() {
        NotificationOutbound msg = new NotificationOutbound(source.getCardName(),"Players who win the next quest will receive 2 extra shields!",source.getCard().getImgSrc(),null);
        NotificationOutboundService.getService().sendInfoNotification(activatedBy,msg,msg);
        waitForTargetTrigger();
    }

    @Override
    protected void onEffectResolution() {
        HashMap<Players,Integer> awardList = new HashMap<Players, Integer>();
        for(Players p : possibleTargerts) {
            awardList.put(p,2);
        }
        NotificationOutbound toAffected = new NotificationOutbound(source.getCardName(),"You have been awarded 2 extra shields for your quest victory! That's right, do a happy dance!",source.getCard().getImgSrc(),null);
        EffectResolverService.getService().playerAwardedShields(awardList);
        for(Players p : awardList.keySet()) {
            NotificationOutboundService.getService().sendGoodNotification(p,toAffected,null);
        }
        nextState();
    }
}
