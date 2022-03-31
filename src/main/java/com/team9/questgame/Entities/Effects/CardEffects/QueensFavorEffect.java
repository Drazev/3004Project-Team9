package com.team9.questgame.Entities.Effects.CardEffects;

import com.team9.questgame.Entities.Effects.EffectResolverService;
import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Effects.TargetSelector;
import com.team9.questgame.Entities.Effects.TargetSelectors.LowestRankSelector;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.gamemanager.record.socket.NotificationOutbound;
import com.team9.questgame.gamemanager.service.NotificationOutboundService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
        HashSet<Players> allPlayers = new HashSet<>(possibleTargerts);
        this.possibleTargerts=targetSelectors.get(0).selectTargets(this.possibleTargerts);
        HashMap<Players,Integer> awardList = new HashMap<>();
        for(Players p : possibleTargerts) {
            awardList.put(p,2);
        }
        EffectResolverService.getService().drawAdventureCards(awardList);
        String msg = "Her majesty the Queen has shown favor towards  ";
        for(int i=0;i<possibleTargerts.size();++i) {
            msg += possibleTargerts.get(i).getName();
            if(possibleTargerts.size()<2) {
                //nothing
            }
            else if (i < possibleTargerts.size() - 2) { //For all until third last entry
                msg+=", ";
            }
            else if(i < possibleTargerts.size()-1) { //If it's second last entry
                msg+=", and ";
            }
        }
        msg += ". She awards them 2 shields!";
        for(Players p : possibleTargerts) {
            NotificationOutboundService.getService().sendGoodNotification(p,new NotificationOutbound(source.getCardName(),msg,source.getCard().getImgSrc(),null),null);
        }
        allPlayers.removeAll(possibleTargerts);
        for(Players p : allPlayers) {
            NotificationOutboundService.getService().sendInfoNotification(p,new NotificationOutbound(source.getCardName(),msg,source.getCard().getImgSrc(),null),null);
        }
        nextState();
    }
}
