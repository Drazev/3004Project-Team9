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
    protected final ArrayList<TargetSelector> targetSelectors;
    protected Cards source;
    protected Players activatedBy;
    protected ArrayList<Players> possibleTargerts;
    protected EffectState state;
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


    public Cards getSource() {
        return source;
    }

    public void setSource(Cards source) {
        this.source = source;
    }

    abstract protected ArrayList<TargetSelector> initTargetSelectors();

    /**
     * Triggers the algorithm that will run the entire effect.
     */
    public void activate(Players activatedBy) throws IllegalEffectStateException {
        if(activatedBy==null) {
            LOG.error(source.getCardName()+" has been activated but source cannot be null!");
            throw new IllegalEffectStateException("An effect cannot be activated without a player source.",this,source);
        }
        this.activatedBy = activatedBy;
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
        this.possibleTargerts=new ArrayList<>(targetedPlayers);
        nextState();
    }

    /**
     * Rest an effect in progress that may be waiting on a trigger
     */
    public void reset() {
        activatedBy=null;
        EffectResolverService.getService().unregisterEffectTriggeredOnQuestCompleted(this);
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
            case TARGET_SELECTION,TRIGGERED -> {
                this.state=EffectState.EFFECT_RESOLUTION;
                LOG.info(source.getCardName()+" state changed to "+this.state+" by "+activatedBy.getName());
                onEffectResolution();
            }
            case EFFECT_RESOLUTION -> {
                this.state=EffectState.RESOLVED;
                LOG.info(source.getCardName()+" state changed to "+this.state+" by "+activatedBy.getName());
                onResolved();
            }
        }
    }

    protected void waitForTrigger() {
        if(this.state!=EffectState.ACTIVATED) {
            throw new IllegalEffectStateException("Effect must be in state "+EffectState.ACTIVATED+" to wait for trigger, but was in "+this.state,this,source);
        }
        this.state=EffectState.TARGET_SELECTION_ON_TRIGGER;
        LOG.info(source.getCardName()+" state changed to "+this.state+" by "+activatedBy.getName());
        EffectResolverService.getService().registerEffectTriggeredOnQuestCompleted(this);
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
        reset();
    }



}
