package com.team9.questgame.Entities.Effects;

import com.team9.questgame.Entities.Players;

import java.util.ArrayList;
import java.util.HashSet;

public interface TargetSelector {
    /**
     * Removes all players that are not valid targets based on target selector
     * criteria. This will change the list given.
     * @param possibleTargets The original list to be filtered
     * @return A new list with all the targets, ranked in order with 0 being the one that most fits
     */
    ArrayList<Players> selectTargets(ArrayList<Players> possibleTargets);
}
