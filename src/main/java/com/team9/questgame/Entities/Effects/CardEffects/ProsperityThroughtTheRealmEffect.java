package com.team9.questgame.Entities.Effects.CardEffects;

import com.team9.questgame.Entities.Effects.EffectResolverService;
import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Effects.TargetSelector;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.gamemanager.record.socket.NotificationOutbound;
import com.team9.questgame.gamemanager.service.NotificationOutboundService;

import java.util.ArrayList;
import java.util.HashMap;

public class ProsperityThroughtTheRealmEffect extends Effects {
    @Override
    protected ArrayList<TargetSelector> initTargetSelectors() {
        return null; //trival, not needed
    }

    @Override
    protected void onActivated() {
        nextState();
    }

    @Override
    protected void onEffectResolution() {
        HashMap<Players,Integer> awardList = new HashMap<Players, Integer>();
        for(Players p : possibleTargerts) {
            awardList.put(p,2);
        }
        EffectResolverService.getService().drawAdventureCards(awardList);
        NotificationOutbound msg = new NotificationOutbound(source.getCardName(),"A mighty power has blessed the land and provided a bountiful harvest. You draw 2 Adventure Cards!",source.getCard().getImgSrc(),null);
        NotificationOutboundService.getService().sendGoodNotification(activatedBy,msg,msg);
        nextState();
    }
}
