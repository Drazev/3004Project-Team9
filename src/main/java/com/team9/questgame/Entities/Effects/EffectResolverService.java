package com.team9.questgame.Entities.Effects;

import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.CardTypes;
import com.team9.questgame.game_phases.GeneralGameController;
import com.team9.questgame.gamemanager.service.OutboundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger LOG;

    HashSet<Effects> triggeredEffects;
    HashMap<Effects,HashSet<DiscardObserver>> activeDiscardObservers;



    private static ApplicationContext context;

    public EffectResolverService() {
        this.triggeredEffects=new HashSet<>();
        activeDiscardObservers = new HashMap<>();
        LOG = LoggerFactory.getLogger(EffectResolverService.class);
    }



    public ArrayList<Players> getPlayerList() {
        return new ArrayList<>(gameController.getPlayers());
    }

    public void playerAwardedShields(HashMap<Players,Integer> targetedPlayers) {
        for(Map.Entry<Players,Integer> e : targetedPlayers.entrySet()) {
            e.getKey().awardShields(e.getValue());
            LOG.info("Player "+e.getKey().getName()+" gains "+e.getValue()+" shields!");
        }
    }

    public HashMap<Players,Boolean> playerLoosesShieldsHashMap(HashMap<Players,Integer> targetedPlayers) {
        HashMap<Players,Boolean> results = new HashMap<>();
        for(Map.Entry<Players,Integer> e : targetedPlayers.entrySet()) {
            results.put(e.getKey(),e.getKey().looseShields(e.getValue()));
            LOG.info("Player "+e.getKey().getName()+" looses "+e.getValue()+" shields!");
        }
        return results;
    }

    public boolean forcePlayerDiscards(Effects effect, HashMap<Players,HashMap<CardTypes,Integer>> targetedPlayers) {
        boolean rc=false;
        for(Map.Entry<Players,HashMap<CardTypes,Integer>> e : targetedPlayers.entrySet()) {
            DiscardObserver ob = new DiscardObserver(effect,e.getKey(),e.getValue());
            e.getKey().getHand().registerDiscardObserver(ob);
            HashSet<DiscardObserver> obList = activeDiscardObservers.get(effect);
            if(obList==null) {
                obList = new HashSet<>();
                activeDiscardObservers.put(effect,obList);
            }
            obList.add(ob);
            rc=true;
        }
        return rc;
    }

    public void drawAdventureCards(HashMap<Players,Integer> targetedPlayers) {
        for(Map.Entry<Players,Integer> e : targetedPlayers.entrySet()) {
            for(int i=0;i<e.getValue();++i) {
                gameController.getADeck().drawCard(e.getKey().getHand());
            }
            LOG.info("Player "+e.getKey().getName()+" draws "+e.getValue()+" adventure cards!");
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
                if(success) {
                    LOG.info("Discarded ALL cards from "+player.getName()+"'s play area!");
                }
                rc = rc | success;
                results.put(player,rc);
                continue;
            }
            if(cardTypeList.contains(CardTypes.ALLY)) {
                success=player.getPlayArea().discardAllAllies();
                if(success) {
                    LOG.info("Discarded "+CardTypes.ALLY+" cards from "+player.getName()+"'s play area!");
                }
                rc = rc ? rc : success;

            }

            if(cardTypeList.contains(CardTypes.AMOUR)) {
                success=player.getPlayArea().discardAllAmour();
                if(success) {
                    LOG.info("Discarded "+CardTypes.AMOUR+" cards from "+player.getName()+"'s play area!");
                }
                rc = rc ? rc : success;
            }

            if(cardTypeList.contains(CardTypes.WEAPON)) {
                success=player.getPlayArea().discardAllWeapons();
                if(success) {
                    LOG.info("Discarded "+CardTypes.WEAPON+" cards from "+player.getName()+"'s play area!");
                }
                rc = rc ? rc : success;
            }
            results.put(player,rc);
        }
        return results;
    }

   public void registerEffectTriggeredOnQuestCompleted(Effects effect) {
        LOG.info("Register triggered card effect "+effect.source.getCardCode());
        triggeredEffects.add(effect);
    }

   public void unregisterEffectTriggeredOnQuestCompleted(Effects effect) {
       LOG.info("UN-Register triggered card effect "+effect.source.getCardCode());
        triggeredEffects.remove(effect);
    }

    public void onQuestCompleted(HashMap<Players, Integer> targetedPlayers) {
        LOG.info("onQuestCompleted triggered");
        playerAwardedShields(targetedPlayers);
        ArrayList<Players> questVictors = new ArrayList<>(targetedPlayers.keySet());
        for(Effects e : triggeredEffects) {
            e.trigger(questVictors);
        }
    }

    public void onDiscardObserverResolution(DiscardObserver observer) {
        observer.getTargetPlayer().getHand().unregisterDiscardObserver(observer);
        HashSet<DiscardObserver> list = activeDiscardObservers.get(observer.getEffect());
        if(list!=null) {
            list.remove(observer);
            if(list.isEmpty()) {
                observer.getEffect().trigger();
            }
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
