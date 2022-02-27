package com.team9.questgame.Entities.cards;

import com.team9.questgame.Entities.Effects.Effects;

public interface CardWithActiveEffect {
    Effects activeEffect=null;

    boolean useActiveEffect();
    String getEffectDescription();

}
