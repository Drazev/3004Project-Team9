package com.team9.questgame.Entities.Effects.TargetSelectors;

import com.team9.questgame.Entities.Effects.TargetSelector;
import com.team9.questgame.Entities.Players;

import java.util.ArrayList;

public class LowestRankSelector implements TargetSelector {

    public ArrayList<Players> selectTargets(ArrayList<Players> possibleTargets) {
        ArrayList<Players> newList = new ArrayList<>();
        Players p1 = possibleTargets.get(0);
        for(Players p : possibleTargets) {
            if(p.getRank().getRankBattlePointValue() < p1.getRank().getRankBattlePointValue()) {
                newList.clear();
                p1=p;
                newList.add(p);
            }
            else if(p.getRank().getRankBattlePointValue() == p1.getRank().getRankBattlePointValue()) {
                newList.add(p);
            }
        }
        return newList;
    }
}
