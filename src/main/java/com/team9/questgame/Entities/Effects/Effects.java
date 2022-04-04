package com.team9.questgame.Entities.Effects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.CardWithEffect;
import com.team9.questgame.exception.IllegalEffectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Effects represent an algorithm that affects players
 * and the entities they control. Any given effect will
 * coordinate with the EffectResolutionService. An effect
 * may have one or more TargetSelectors that will be used
 * to gather targets from the EffectResolutionService.
 *
 * The effect conducts an algorithm to determine if and when
 * targets are selected, and then if and when one or more outcomes
 * occur.
 *
 * The effect will again use the EffectResolutionService to resolve one or more
 * EffectOutcomes. Sometimes the Effect may use the Resolution service to delay
 * activation until a specific trigger such as the end of a quest phase.
 */
public abstract class Effects {
    protected final ArrayList<TargetSelector> targetSelectors;
    protected CardWithEffect source;
    protected Players activatedBy;
    protected ArrayList<Players> possibleTargerts;
    protected EffectState state;
    protected EffectObserver observer;
    protected long pendingRequestID;
    @JsonIgnore
    static protected Logger LOG= LoggerFactory.getLogger(Effects.class);

    /**
     * Creates a new Effect Recipe
     */
    protected Effects() {
        this.targetSelectors = initTargetSelectors();
        this.source = null;
        this.activatedBy=null;
        this.possibleTargerts=null;
        this.state = EffectState.INACTIVE;
    }


    public CardWithEffect getSource() {
        return source;
    }

    public Players getActivatedBy() {
        return activatedBy;
    }

    public EffectState getState() {
        return state;
    }

    public void setSource(CardWithEffect source) {
        this.source = source;
    }



    abstract protected ArrayList<TargetSelector> initTargetSelectors();

    /**
     * Triggers the algorithm that will run the entire effect.
     */
    public void activate(EffectObserver observer,Players activatedBy) throws IllegalEffectStateException {
        if(activatedBy==null || observer==null) {
            LOG.error(source.getCardName()+" has been activated but source or EffectObserver cannot be null!");
            throw new IllegalEffectStateException("An effect cannot be activated without a player source.",this,source);
        }
        else if(this.state!=EffectState.INACTIVE) { //Handle duplicate requests
            LOG.warn(source.getCardName()+" activation request received but effect is active and in the "+state+" state.");
            return;
        }
        this.activatedBy = activatedBy;
        this.observer = observer;
        LOG.info(source.getCardName()+" has been activated by "+activatedBy.getName());

        nextState();
    }

    /**
     * Reactivate effects that waiting for a trigger before resolving
     */
    public void trigger(ArrayList<Players> targetedPlayers) throws IllegalEffectStateException {
        if(state!=EffectState.TRIGGER_TARGET_SELECTION) {
            reset();
            throw new IllegalEffectStateException(String.format("Effect must be in state %s  to be triggered with a target list but was in state %s",EffectState.TRIGGER_TARGET_SELECTION,state),this,source);
        }
        this.possibleTargerts=new ArrayList<>(targetedPlayers);
        nextState();
    }

    /**
     * Reactivate effects that waiting for a trigger before resolving
     */
    public void trigger() throws IllegalEffectStateException {
        if(state!=EffectState.EFFECT_RESOLUTION_PLAYER_RESPONSE) {
            reset();
            throw new IllegalEffectStateException(String.format("Effect must be in state %s  to be triggered with no args but was in state %s",EffectState.EFFECT_RESOLUTION_PLAYER_RESPONSE,state),this,source);
        }
        nextState();
    }

    public void trigger(long resolvedRequestID) {
        if(state!=EffectState.TRIGGER_TARGET_SELECTION_REQUEST_SUBMITTED) {
            reset();
            throw new IllegalEffectStateException(String.format("Effect must be in state %s  to be triggered with a resolvedRequestID but was in state %s",EffectState.TRIGGER_TARGET_SELECTION_REQUEST_SUBMITTED,state),this,source);
        }
        else if(resolvedRequestID!=pendingRequestID) {
            reset();
            throw new IllegalEffectStateException(String.format("Effect triggered using a different request id (%d) than the original request (%d).",resolvedRequestID,pendingRequestID),this,source);
        }
        nextState();
    }

    /**
     * Rest an effect in progress that may be waiting on a trigger
     */
    public void reset() {
        activatedBy=null;
        observer=null;
        EffectResolverService.getService().unregisterEffectTriggeredOnQuestCompleted(this);
        this.state=EffectState.INACTIVE;
        LOG.info("Effect "+source.getCardCode()+" was REST to status "+state);
    }

    /**
     * Manages lifecycle of effect.
     * Must be called once each stage has completed to call next stage.
     * If in activated state can alternativly call waitForTrigger() instead.
     */
    protected void nextState() {
        switch(state) {
            case INACTIVE -> {
                this.state=EffectState.ACTIVATED;
                onActivated();
            }
            case ACTIVATED -> {
                this.state =  EffectState.TARGET_SELECTION;
                LOG.info(source.getCardName()+" state changed to "+this.state+" by "+activatedBy.getName());
                onTargetSelection();
            }
            case TRIGGER_TARGET_SELECTION -> {
                this.state=EffectState.TRIGGER_EFFECT_RESOLUTION;
                LOG.info(source.getCardName()+" state changed to "+this.state+" by "+activatedBy.getName());
                onEffectResolution();
            }
            case TARGET_SELECTION -> {
                this.state=EffectState.EFFECT_RESOLUTION;
                LOG.info(source.getCardName()+" state changed to "+this.state+" by "+activatedBy.getName());
                onEffectResolution();
            }
            case EFFECT_RESOLUTION,EFFECT_RESOLUTION_PLAYER_RESPONSE,TRIGGER_TARGET_SELECTION_REQUEST_SUBMITTED -> {
                this.state=EffectState.RESOLVED;
                LOG.info(source.getCardName()+" state changed to "+this.state+" by "+activatedBy.getName());
                onResolved();
            }
            case TRIGGER_EFFECT_RESOLUTION -> {
                this.state = EffectState.TRIGGER_RESOLVED;
                LOG.info(source.getCardName()+" state changed to "+this.state+" by "+activatedBy.getName());
                onTriggerResolved();
            }
        }
    }

    protected void waitForTargetTrigger() {
        if(this.state!=EffectState.ACTIVATED) {
            throw new IllegalEffectStateException("Effect must be in state "+EffectState.ACTIVATED+" to wait for trigger, but was in "+this.state,this,source);
        }
        this.state=EffectState.TRIGGER_TARGET_SELECTION;
        LOG.info(source.getCardName()+" state changed to "+this.state+" by "+activatedBy.getName());
        EffectResolverService.getService().registerEffectTriggeredOnQuestCompleted(this);

        observer.onEffectResolvedWithDelayedTrigger(source);
    }

    protected void waitForTargetSelectionRequest(TargetSelectionRequestTypes type,String message) {
        if(this.state!=EffectState.ACTIVATED) {
            throw new IllegalEffectStateException("Effect must be in state "+EffectState.ACTIVATED+" to wait for trigger, but was in "+this.state,this,source);
        }
        this.state=EffectState.TRIGGER_TARGET_SELECTION_REQUEST_SUBMITTED;
        LOG.info(source.getCardName()+" state changed to "+this.state+" by "+activatedBy.getName());
        pendingRequestID=EffectResolverService.getService().targetSelectionRequest(this,type,message);
    }

    protected void waitForResolutionTrigger() {
        if(this.state!=EffectState.EFFECT_RESOLUTION) {
            throw new IllegalEffectStateException("Effect must be in state "+EffectState.TRIGGER_TARGET_SELECTION+" to wait for resolution trigger, but was in "+this.state,this,source);
        }
        this.state=EffectState.EFFECT_RESOLUTION_PLAYER_RESPONSE;
        LOG.info(source.getCardName()+" state changed to "+this.state+" by "+activatedBy.getName());
    }

    /**
     * Setup stage for effect
     */
    abstract protected void onActivated();

    /**
     * Automatically gather possible targets from the EffectResolverService
     */
    protected void onTargetSelection() {
        this.possibleTargerts = new ArrayList<>(EffectResolverService.getService().getPlayerList());
        nextState();
    }

    /**
     * Algorithm to manage target selection and effect resolution
     * using targetSelectors and effectOutcomes. Determine how many different
     * target sets are affected by which outcomes and execute them using
     * the EffectResolverService.
     */
    abstract protected void onEffectResolution();

    /**
     * Cleanup and prepare for the next time card is used.
     */
    protected void onResolved() {
        LOG.info(source.getCardName()+" has been resolved by "+activatedBy.getName());
        observer.onEffectResolved(source);
        reset();
    }

    /**
     * Cleanup and prepare for the next time card is used.
     */
    protected void onTriggerResolved() {
        LOG.info(source.getCardName()+" has had it's trigger resolved by "+activatedBy.getName());
        reset();
    }

}
