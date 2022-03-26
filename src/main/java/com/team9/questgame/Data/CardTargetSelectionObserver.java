package com.team9.questgame.Data;

import com.team9.questgame.Entities.Effects.Effects;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.CardWithEffect;
import com.team9.questgame.game_phases.utils.PlayerTurnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CardTargetSelectionObserver {
    private static Logger LOG = LoggerFactory.getLogger(CardTargetSelectionObserver.class);
    private final long requestID;
    private final Players player;
    private final Effects effect;
    private final CardWithEffect card;
    private CardTargetSelection selection;
    private boolean isResolved;



    public CardTargetSelectionObserver(long requestID, Players player, Effects effect) {
        this.requestID = requestID;
        this.player = effect.getActivatedBy();
        this.effect = effect;
        this.card = effect.getSource();
    }

    public boolean selectTarget(CardTargetSelection selection) {
        if(selection == null) {
            return false;
        }
        else if(selection.requestID()!=requestID) {
            LOG.warn("TargetSelection "+selection.requestID()+" rejected. REASON: \"requestID\" does not match expected: "+requestID+", provided:  "+selection.requestID());
            return false;
        }
        else if(selection.requestPlayerID()!=player.getPlayerId()) {
            LOG.warn("TargetSelection "+selection.requestID()+" rejected. REASON: Requesting \"playerID\" does not match expected: "+player.getPlayerId()+", provided:  "+selection.requestPlayerID());
            return false;
        }
        else if(selection.requestCardCode()!=card.getCardCode()) {
            LOG.warn("TargetSelection "+selection.requestID()+" rejected. REASON: \"cardCode\" does not match expected: "+card.getCardCode()+", provided:  "+selection.requestCardCode());
            return false;
        }

        return true;
//        this.selecton = selection;
    }
}
