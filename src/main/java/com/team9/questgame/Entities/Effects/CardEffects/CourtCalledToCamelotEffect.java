package com.team9.questgame.Entities.Effects.CardEffects;

import com.team9.questgame.Entities.Effects.EffectResolverService;
import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Effects.TargetSelector;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.CardTypes;
import com.team9.questgame.gamemanager.record.socket.NotificationOutbound;
import com.team9.questgame.gamemanager.service.NotificationOutboundService;

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
        NotificationOutbound toAffected = new NotificationOutbound(source.getCardName(),"The King has summoned all the court back to camelot. \n All your allies have been discarded!",source.getCard().getImgSrc(),null);
        NotificationOutboundService.getService().sendBadNotification(activatedBy,toAffected,toAffected);
        nextState();
    }
}
