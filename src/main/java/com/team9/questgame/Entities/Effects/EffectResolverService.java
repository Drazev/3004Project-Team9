package com.team9.questgame.Entities.Effects;

import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.AllCardCodes;
import com.team9.questgame.Entities.cards.CardTypes;
import com.team9.questgame.Entities.cards.Cards;
import com.team9.questgame.exception.IllegalEffectStateException;
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
    HashMap<Players,CardDiscardList> forcedDiscardList;


    public EffectResolverService() {
        this.triggeredEffects=new HashSet<>();
        this.forcedDiscardList=new HashMap<>();
    }

    public void loadTargetSelector(TargetSelector selector) {
        selector.setPossibleTargets(gameController.getPlayers());
    }

    public boolean playerDiscardsFromPlaySpecificCardIfAvailable(Players target, AllCardCodes targetCard) {
        return target.getPlayArea().destroyCardIfPresent(targetCard);
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

    public void registerForcePlayerDiscards(HashSet<CardDiscardList> cardDiscardList) {

        for(CardDiscardList dl : cardDiscardList) {
            if(forcedDiscardList.containsKey(dl.getTarget()))
            {
               throw new IllegalEffectStateException("A discard list included player "+dl.getTarget().getName()+" but the player was already in the process of discarding. This should never happen!",dl.getSource(),dl.getSource().getCardSource());
            }
            forcedDiscardList.put(dl.getTarget(),dl);
        }
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

    public void notifyCardDiscarded(Players player, Cards card) {
        if(forcedDiscardList.containsKey(player)) {
            forcedDiscardList.get(player).reportDiscardedCard(card);
        }
    }

    public void onCardDiscardListResolved(CardDiscardList list) {
        if(forcedDiscardList.remove(list.getTarget(),list))
        {
            list.getSource().resolveEffect();
        }
    }

}
