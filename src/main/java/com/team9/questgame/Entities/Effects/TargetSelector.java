package com.team9.questgame.Entities.Effects;

import com.team9.questgame.Entities.Players;

import java.util.ArrayList;
import java.util.HashSet;

public interface TargetSelector {
    void setPossibleTargets(ArrayList<Players> possibleTargets);
    HashSet<Players> computeTargets();
}
