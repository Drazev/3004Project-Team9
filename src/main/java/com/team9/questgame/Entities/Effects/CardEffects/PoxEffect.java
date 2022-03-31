package com.team9.questgame.Entities.Effects.CardEffects;

import com.team9.questgame.Entities.Effects.EffectResolverService;
import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Effects.TargetSelector;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.gamemanager.record.socket.NotificationOutbound;
import com.team9.questgame.gamemanager.service.NotificationOutboundService;

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
        String msg = String.format("Pox has ravaged the land and many fall ill. You looses 1 shield!",activatedBy.getName());
        NotificationOutbound msgOut = new NotificationOutbound(source.getCardName(),msg,source.getCard().getImgSrc(),null);
        NotificationOutboundService.getService().sendBadNotification(activatedBy,null,msgOut);
        NotificationOutbound msgOutNotAffected = new NotificationOutbound(source.getCardName(),"Pox has ravaged the land and many fall ill. However mysterious forces have protected your lands from the same fate as others!",source.getCard().getImgSrc(),null);
        NotificationOutboundService.getService().sendInfoNotification(activatedBy,msgOutNotAffected,null);
        nextState();
    }
}
