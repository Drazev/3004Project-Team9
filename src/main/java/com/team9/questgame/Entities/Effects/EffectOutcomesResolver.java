package com.team9.questgame.Entities.Effects;

import com.team9.questgame.Entities.Players;

import java.util.HashSet;

public interface EffectOutcomesResolver {

    /**
     * Apply effect outcome to target player list.
     * @param targets A set of targets which will be subject to the effect
     * @return True if effect was successfully applied to all targets. False if it failed to apply to any one or more targets.
     */
    boolean applyEffect(HashSet<Players> targets);

    void getSuccessMessage();

    void getFailureMessage();

    boolean isNegativeEffect();
}
