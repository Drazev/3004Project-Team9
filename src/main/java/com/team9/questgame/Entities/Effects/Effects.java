package com.team9.questgame.Entities.Effects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.Cards;
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
    private final ArrayList<TargetSelector> targetSelectors;
    private final Cards source;
    private Players activatedBy;
    private EffectState state;
    @JsonIgnore
    static protected Logger LOG= LoggerFactory.getLogger(Effects.class);
    @JsonIgnore
    static protected EffectResolverService effectResolver=ApplicationContextHolder.getContext().getBean(EffectResolverService .class);

    /**
     * Creates a new Effect Recipe
     * @param targetSelectors A selector algorithm used by EffectResolutionService to determine those in the effect area.
     * @param source Card that was the source of the effect.
     */
    protected Effects(ArrayList<TargetSelector> targetSelectors, Cards source) {
        this.targetSelectors = targetSelectors;
        this.source = source;
        this.activatedBy=null;
        this.state = EffectState.INACTIVE;
    }

    /**
     * Triggers the algorithm that will run the entire effect.
     */
    public void activate(Players activatedBy) throws IllegalEffectStateException {
        if(activatedBy==null) {
            LOG.error(source.getCardName()+" has been activated but source cannot be null!");
            throw new IllegalEffectStateException("An effect cannot be activated without a player source.",this,source);
        }
        this.activatedBy = activatedBy;
        this.state=EffectState.ACTIVATED;
        LOG.info(source.getCardName()+" has been activated by "+activatedBy.getName());

        nextState();
    }

    /**
     * Reactivate effects that waiting for a trigger before resolving
     */
    public void trigger(ArrayList<Players> targetedPlayers) throws IllegalEffectStateException {
        if(state!=EffectState.TARGET_SELECTION_ON_TRIGGER) {
            throw new IllegalEffectStateException("Effect must be waiting on a trigger to be triggered. This effect was in the state: "+state,this,source);
        }
        for(TargetSelector t : targetSelectors) {
            t.setPossibleTargets(targetedPlayers);
        }
        nextState();
    }

    /**
     * Rest an effect in progress that may be waiting on a trigger
     */
    public void reset() {
        activatedBy=null;
        effectResolver.unregisterEffectTriggeredOnQuestCompleted(this);
        this.state=EffectState.INACTIVE;
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
            case TARGET_SELECTION_ON_TRIGGER -> {
                this.state=EffectState.TRIGGERED;
                LOG.info(source.getCardName()+" state changed to "+this.state+" by "+activatedBy.getName());
                onTriggered();
            }
            case TARGET_SELECTION -> {
                this.state=EffectState.EFFECT_RESOLUTION;
                LOG.info(source.getCardName()+" state changed to "+this.state+" by "+activatedBy.getName());
                onEffectResolution();
            }
            case RESOLVED -> {
                this.state=EffectState.RESOLVED;
                LOG.info(source.getCardName()+" state changed to "+this.state+" by "+activatedBy.getName());
                onEffectResolution();
            }
        }
    }

    protected void waitForTrigger() {
        if(this.state!=EffectState.ACTIVATED) {
            throw new IllegalEffectStateException("Effect must be in state "+EffectState.ACTIVATED+" to wait for trigger, but was in "+this.state,this,source);
        }
        this.state=EffectState.TARGET_SELECTION_ON_TRIGGER;
        LOG.info(source.getCardName()+" state changed to "+this.state+" by "+activatedBy.getName());
        effectResolver.registerEffectTriggeredOnQuestCompleted(this);
    }

    /**
     * Setup stage for effect
     */
    abstract protected void onActivated();

    /**
     * If effect was on delayed trigger, do this
     * after the trigger but before the effect selects targets
     *
     * Overriding this is optional
     */
    protected void onTriggered(){
        nextState();
    }

    /**
     * Automatically gather possible targets from the EffectResolverService
     */
    protected void onTargetSelection() {
        for(TargetSelector ts : targetSelectors) {
            effectResolver.loadTargetSelector(ts);
        }
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
        reset();
    }



}
