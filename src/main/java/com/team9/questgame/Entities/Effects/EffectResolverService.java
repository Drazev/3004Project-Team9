package com.team9.questgame.Entities.Effects;

import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.CardTypes;
import com.team9.questgame.game_phases.GeneralGameController;
import com.team9.questgame.gamemanager.service.OutboundService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Service
public class EffectResolverService implements ApplicationContextAware {
    @Autowired
    OutboundService outService;
    @Autowired
    GeneralGameController gameController;

    HashSet<Effects> triggeredEffects;

    private static ApplicationContext context;

    public EffectResolverService() {
        this.triggeredEffects=new HashSet<>();
    }

    public ArrayList<Players> getPlayerList() {
        return new ArrayList<>(gameController.getPlayers());
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
            boolean success = false;
            if(cardTypeList==null) {
                success=player.getPlayArea().discardAllCards();
                rc = rc | success;
                results.put(player,rc);
                continue;
            }
            if(cardTypeList.contains(CardTypes.ALLY)) {
                success=player.getPlayArea().discardAllAllies();
                rc = rc ? rc : success;

            }

            if(cardTypeList.contains(CardTypes.AMOUR)) {
                success=player.getPlayArea().discardAllAmour();
                rc = rc ? rc : success;
            }

            if(cardTypeList.contains(CardTypes.WEAPON)) {
                success=player.getPlayArea().discardAllWeapons();
                rc = rc ? rc : success;
            }
            results.put(player,rc);
        }
        return results;
    }

   public void registerEffectTriggeredOnQuestCompleted(Effects effect) {
        triggeredEffects.add(effect);
    }

   public void unregisterEffectTriggeredOnQuestCompleted(Effects effect) {
        triggeredEffects.remove(effect);
    }

    public void onQuestCompleted(HashMap<Players, Integer> targetedPlayers) {
        playerAwardedShields(targetedPlayers);
        ArrayList<Players> questVictors = new ArrayList<>(targetedPlayers.keySet());
        for(Effects e : triggeredEffects) {
            e.trigger(questVictors);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static EffectResolverService getService() {
        return context.getBean(EffectResolverService.class);
    }
}
