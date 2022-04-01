package com.team9.questgame.game_phases.event;

import com.team9.questgame.Entities.Effects.EffectObserver;
import com.team9.questgame.Entities.cards.CardWithEffect;
import com.team9.questgame.Entities.cards.EventCards;
import com.team9.questgame.game_phases.GamePhases;
import com.team9.questgame.game_phases.GeneralGameController;
import com.team9.questgame.game_phases.GeneralStateE;
import com.team9.questgame.game_phases.GeneralStateMachine;
import com.team9.questgame.game_phases.utils.PlayerTurnService;
import com.team9.questgame.game_phases.utils.StateMachineObserver;
import com.team9.questgame.gamemanager.service.OutboundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventPhaseController implements GamePhases<EventCards,EventPhaseStatesE>, EffectObserver<EventCards>, StateMachineObserver<GeneralStateE> {
    private final Logger LOG;
    private final GeneralGameController generalController;
    private EventCards card;
    private PlayerTurnService turnService;

    private EventPhaseStatesE previousState;
    private EventPhaseStatesE currentState;

    public EventPhaseController(GeneralGameController generalController) {
        this(generalController,null);
    }

    public EventPhaseController(GeneralGameController generalController,EventCards card) {
        LOG = LoggerFactory.getLogger(EventPhaseController.class);
        this.generalController = generalController;
        this.card=card;
        this.currentState = EventPhaseStatesE.READY;
        GeneralStateMachine.getService().registerObserver(this);
    }


    @Override
    public boolean receiveCard(EventCards card) {
        if(card==null || currentState!=EventPhaseStatesE.NOT_STARTED) {
            return false;
        }
        if(this.card!=null) {
            LOG.warn("Event Phase Controller received a card when it already had a card!");
        }
        this.card = card;
        this.currentState = EventPhaseStatesE.READY;
        return true;
    }

    @Override
    public void discardCard(EventCards card) {
        card.discardCard();
        this.card=null;
    }

    @Override
    public boolean playCard(EventCards card) {
        card.activate(this,turnService.getPlayerTurn());
        nextState();
        return true;
    }

    @Override
    public void onGameReset() {
        this.currentState=EventPhaseStatesE.READY;
        if(card!=null) {
            discardCard(card);
        }
        turnService=null;
        GeneralStateMachine.getService().unregisterObserver(this);
    }

    @Override
    public EventPhaseStatesE getCurrState() {
        return currentState;
    }

    @Override
    public void startPhase(PlayerTurnService playerTurnService) {
        if(this.currentState!=EventPhaseStatesE.READY) {

        }
        else if(playerTurnService==null) {

        }

        this.turnService=playerTurnService;
        nextState();
    }

    @Override
    public void endPhase() {
        onGameReset();
        OutboundService.getService().broadcastEventPhaseEnded();
        generalController.requestPhaseEnd();
    }

    @Override
    public EventCards getCard() {
        return card;
    }

    //Simple state machine
    public void nextState() {
        switch(currentState) {
            case READY -> {
                this.currentState = EventPhaseStatesE.CARD_ACTIVATION;
                LOG.info("EventPhase Card Activation for player "+turnService.getPlayerTurn().getName()+", State: "+currentState);
                OutboundService.getService().broadcastEventPhaseStart();
                playCard(card);
            }
            case CARD_ACTIVATION -> {
                this.currentState = EventPhaseStatesE.PLAYER_RESPONSE;
                LOG.info("EventPhase Waiting for effect to resolve "+turnService.getPlayerTurn().getName()+", State: "+currentState);
                //Wait for onEffectResolved() call
            }
            case PLAYER_RESPONSE -> {
                this.currentState = EventPhaseStatesE.ENDED;
                endPhase();
            }
        };
    }

    @Override
    public void onEffectResolved(CardWithEffect resolvedCard) {
        if(resolvedCard==card) {
            discardCard(card);
            nextState();
        }
        else {
            LOG.warn("EventPhaseController::onEffectResolved returned a card that didn't match the current event phase's card.");
        }
    }

    @Override
    public void onEffectResolvedWithDelayedTrigger(CardWithEffect resolvedCard) {
        if(resolvedCard==card) {
            nextState();
        }
        else {
            LOG.warn("EventPhaseController::onEffectResolvedWithDelayedTrigger returned a card that didn't match the current event phase's card.");
        }
    }

    @Override
    public void observerStateChanged(GeneralStateE newState) {
        if (newState == GeneralStateE.PLAYER_HAND_OVERSIZE) {
            this.previousState = this.currentState;
            this.currentState = EventPhaseStatesE.BLOCKED;
        } else if (this.currentState == EventPhaseStatesE.BLOCKED) {
            this.endPhase();
        }

    }
}
