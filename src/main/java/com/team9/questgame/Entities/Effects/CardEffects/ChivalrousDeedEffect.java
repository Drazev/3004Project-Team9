package com.team9.questgame.Entities.Effects.CardEffects;

import com.team9.questgame.Entities.Effects.EffectResolverService;
import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Effects.TargetSelector;
import com.team9.questgame.Entities.Effects.TargetSelectors.LeastShieldsSelector;
import com.team9.questgame.Entities.Effects.TargetSelectors.LowestRankSelector;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.exception.IllegalEffectStateException;
import com.team9.questgame.gamemanager.record.socket.NotificationOutbound;
import com.team9.questgame.gamemanager.service.NotificationOutboundService;

import java.util.ArrayList;
import java.util.HashMap;

public class ChivalrousDeedEffect extends Effects {
    /**
     * Creates a new Effect Recipe
     *
     */
    public ChivalrousDeedEffect() {
        super();
    }

    @Override
    protected ArrayList<TargetSelector> initTargetSelectors() {
        ArrayList<TargetSelector> ts = new ArrayList<>();
        ts.add(new LowestRankSelector());
        ts.add(new LeastShieldsSelector());
        return ts;
    }

    @Override
    protected void onActivated() {
        LOG.info(source.getCardName()+" has activated effect");
        nextState();
    }

    @Override
    protected void onEffectResolution() {
        for(int i=0;i<targetSelectors.size();++i) {
            this.possibleTargerts = targetSelectors.get(i).selectTargets(this.possibleTargerts);
        }
        if(this.possibleTargerts.size()<1) {
            throw new IllegalEffectStateException("Target selection failed, no target found",this,source);
        }
        NotificationOutbound toAffected = new NotificationOutbound(source.getCardName(),"Your noble deeds haven given you great acclaim throughout the land. \n You receive 3 shields!",source.getCard().getImgSrc(),null);
        HashMap<Players,Integer> rewardList = new HashMap<>();
        for(Players p : possibleTargerts) {
            rewardList.put(p,3);
            NotificationOutboundService.getService().sendGoodNotification(p,toAffected,null);
            LOG.info(p.getName()+" has been awarded 3 shields from "+source.getCardName()+" and now has "+p.getShields()+" shields.");
        }
        EffectResolverService.getService().playerAwardedShields(rewardList);
        nextState();
    }
}
