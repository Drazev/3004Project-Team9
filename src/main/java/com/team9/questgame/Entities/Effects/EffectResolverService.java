package com.team9.questgame.Entities.Effects;

import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.CardTypes;
import com.team9.questgame.game_phases.GeneralGameController;
import com.team9.questgame.gamemanager.service.InboundService;
import com.team9.questgame.gamemanager.service.OutboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Service
public class EffectResolverService {
    @Autowired
    OutboundService outService;
    @Autowired
    InboundService inService;
    @Autowired
    GeneralGameController gameController;

    HashSet<Effects> triggeredEffects;

    public EffectResolverService() {
        this.triggeredEffects=new HashSet<>();
    }

    public void loadTargetSelector(TargetSelector selector) {
        selector.setPossibleTargets(gameController.getPlayers());
    }

    public void playerAwardedShields(HashMap<Players,Integer> targetedPlayers) {
        for(Map.Entry<Players,Integer> e : targetedPlayers.entrySet()) {
            e.getKey().awardShields(e.getValue());
        }
    }

    public HashMap<Players,Boolean> playerLoosesShieldsHashMap(HashMap<Players,Integer> targetedPlayers) {
        HashMap<Players,Boolean> results = new HashMap<>();
        for(Map.Entry<Players,Integer> e : targetedPlayers.entrySet()) {
            results.put(e.getKey(),e.getKey().looseShields(e.getValue()));
        }
        return results;
    }

    public boolean forcePlayerDiscards(HashMap<Players,HashMap<CardTypes,Integer>> targetedPlayers) {
        return true; //TODO: Implement this algorithm
    }

    public void drawAdventureCards(HashMap<Players,Integer> targetedPlayers) {
        for(Map.Entry<Players,Integer> e : targetedPlayers.entrySet()) {
            for(int i=0;i<e.getValue();++i) {
                gameController.getADeck().drawCard(e.getKey().getHand());
            }
        }
    }

    public HashMap<Players,Boolean> playerDiscardsAllCardsFromPlay(HashSet<Players> targetedPlayers) {
        return playerDiscardsAllCardsFromPlay(targetedPlayers,null);
    }

    public HashMap<Players,Boolean> playerDiscardsAllCardsFromPlay(HashSet<Players> targetedPlayers,HashSet<CardTypes> cardTypeList ) {
        HashMap<Players,Boolean> results = new HashMap<>();
        for(Players player : targetedPlayers) {
            boolean rc = false; //True if any cards were successfully discarded
            if(cardTypeList==null) {
                rc =player.getPlayArea().discardAllCards();
                results.put(player,rc);
                continue;
            }
            if(cardTypeList.contains(CardTypes.ALLY)) {
                rc = results.put(player,player.getPlayArea().discardAllAllies()) | rc;
            }

            if(cardTypeList.contains(CardTypes.AMOUR)) {
                rc = results.put(player,player.getPlayArea().discardAllAmour()) | rc;
            }

            if(cardTypeList.contains(CardTypes.WEAPON)) {
                rc = results.put(player,player.getPlayArea().discardAllWeapons()) | rc;
            }
            results.put(player,rc);
        }
        return results;
    }

    void registerEffectTriggeredOnQuestCompleted(Effects effect) {
        triggeredEffects.add(effect);
    }

    void unregisterEffectTriggeredOnQuestCompleted(Effects effect) {
        triggeredEffects.remove(effect);
    }

    void onQuestCompleted(ArrayList<Players> targetedPlayers) {
        for(Effects e : triggeredEffects) {
            e.trigger(targetedPlayers);
        }
    }

}
