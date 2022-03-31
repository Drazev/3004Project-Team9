package com.team9.questgame.Entities.Effects.CardEffects;

import com.team9.questgame.Entities.Effects.EffectResolverService;
import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Effects.TargetSelector;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.gamemanager.record.socket.NotificationOutbound;
import com.team9.questgame.gamemanager.service.NotificationOutboundService;

import java.util.ArrayList;
import java.util.HashMap;

public class PlagueEffect extends Effects {
    @Override
    protected ArrayList<TargetSelector> initTargetSelectors() {
        return null; //Trivial case, not needed
    }

    @Override
    protected void onActivated() {
        nextState();
    }

    @Override
    protected void onEffectResolution() {
        HashMap<Players,Integer> targetList = new HashMap<>();
        targetList.put(activatedBy,2);
        EffectResolverService.getService().playerLoosesShieldsHashMap(targetList);
        String msg = String.format("A plague has ravaged the land and killed many people. %s looses 2 shields!",activatedBy.getName());
        NotificationOutbound msgOut = new NotificationOutbound(source.getCardName(),msg,source.getCard().getImgSrc(),null);
        NotificationOutboundService.getService().sendBadNotification(activatedBy,msgOut,null);
        NotificationOutboundService.getService().sendInfoNotification(activatedBy,null,msgOut);
        nextState();
    }
}
