package com.team9.questgame.Entities.Effects.CardEffects;

import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Effects.TargetSelectionRequestTypes;
import com.team9.questgame.Entities.Effects.TargetSelector;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.gamemanager.record.socket.NotificationOutbound;
import com.team9.questgame.gamemanager.service.NotificationOutboundService;

import java.util.ArrayList;

public class MerlinEffect extends Effects {
    @Override
    protected ArrayList<TargetSelector> initTargetSelectors() {
        return null;
    }

    @Override
    protected void onActivated() {
        waitForTargetSelectionRequest(TargetSelectionRequestTypes.STAGE_TARGET_SELECTION,"Select a stage for Merlin to reveal!");
    }

    @Override
    protected void onEffectResolution() {
        NotificationOutbound msg = new NotificationOutbound(source.getCardName(),"Rumor has it that Merlin has had visions of the upcoming battle and has shared that prophecy with those he trusts!",source.getCard().getImgSrc(),null);
        NotificationOutboundService.getService().sendGoodNotification(activatedBy,msg,null);
        for(Players p : possibleTargerts) {
            if(p!=activatedBy) {
                NotificationOutboundService.getService().sendInfoNotification(p,msg,null);
            }
        }

    }
}
