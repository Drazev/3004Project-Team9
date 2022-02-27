package com.team9.questgame.Entities.Effects;

import java.util.Collections;
import java.util.HashMap;

public enum ActiveEffectTypes {
    /**
    PREVIEW_STAGE(
            GamePhases.QUEST,
            "Player may preview any one stage per Quest",
            AbstractTargetTypes.PLAYER_CURRENT,
            ActiveEffectResolutionSequencing.INSTANT,
            HashMap::new::put(GamePhases.QUEST,CardEffectActions.VIEW_QUEST_STAGE)
    ), //MERLIN
    MORDRED_REMOVE_TARGET_ALLY(GamePhases.ANY, "Use as a Foe or sacrifice at any time to remove any player's Ally from play",CardTypes.ALLY, sequence, effectSequence), //MORDRED

    ;

     private final GamePhases requiredPhase;
     private final String description;
     private final TargetableEnum targetEnum;
     private final ActiveEffectResolutionSequencing sequence;
     private final EnumMap<argetableEnum,CardEffectActions> effectSequence = Collections.unmodifiableMap();




    ActiveEffectTypes(GamePhases requiredPhase, String description, TargetableEnum target, ActiveEffectResolutionSequencing sequence, HashMap<TargetableEnum, CardEffectActions> effectSequence) {
        this.requiredPhase = requiredPhase;
        this.description = description;
        this.targetEnum = target;
        this.sequence = sequence;
        this.effectSequence = effectSequence;
    }

    public GamePhases getRequiredPhase() {
        return requiredPhase;
    }

    public String getDescription() {
        return description;
    }

    public TargetableEnum getTargetEnum() {
        return targetEnum;
    }
     **/
}
