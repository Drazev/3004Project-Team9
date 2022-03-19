package com.team9.questgame.Entities.Effects.TargetSelectors;

import com.team9.questgame.Entities.Effects.TargetSelector;
import com.team9.questgame.Entities.Players;

import java.util.ArrayList;

public class LeastShieldsSelector implements TargetSelector {
    @Override
    public ArrayList<Players> selectTargets(ArrayList<Players> possibleTargets) {
        ArrayList<Players> newList = new ArrayList<>();
        Players p1 = possibleTargets.get(0);
        for(Players p : possibleTargets) {
            if(p.getShields() < p1.getShields()) {
                newList.clear();
                p1=p;
                newList.add(p);
            }
            else if(p.getShields() == p1.getShields()) {
                newList.add(p);
            }
        }
        return newList;
    }
}
