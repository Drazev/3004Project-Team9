package com.team9.questgame.Entities.Effects;

import com.team9.questgame.Data.TargetSelectionRequest;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.CardTypes;
import com.team9.questgame.exception.IllegalEffectStateException;
import com.team9.questgame.game_phases.GeneralGameController;
import com.team9.questgame.game_phases.quest.QuestPhaseController;
import com.team9.questgame.gamemanager.record.socket.CardTargetSelectionResponse;
import com.team9.questgame.gamemanager.record.socket.NotificationOutbound;
import com.team9.questgame.gamemanager.record.socket.StageTargetSelectionResponse;
import com.team9.questgame.gamemanager.service.NotificationOutboundService;
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
    GeneralGameController gameController;

    QuestPhaseController questController;

    @Autowired
    NotificationOutboundService notifyService;

    static long nextRequestID=0;

    Logger LOG;

    HashSet<Effects> triggeredEffects;
    HashMap<Effects,HashSet<DiscardObserver>> activeDiscardObservers;
    HashMap<Long,Effects> targetSelectionRequestIdToEffects;



    private static ApplicationContext context;

    public EffectResolverService() {
        this.triggeredEffects=new HashSet<>();
        activeDiscardObservers = new HashMap<>();
        targetSelectionRequestIdToEffects = new HashMap<>();
        LOG = LoggerFactory.getLogger(EffectResolverService.class);
    }



    public ArrayList<Players> getPlayerList() {
        return new ArrayList<>(gameController.getPlayers());
    }

    /**
     * Awards a group of players an associated amount of shields provided by a map.
     *
     * @param targetedPlayers A map indicating which players are affected and how many shields to award for each player.
     */
    public void playerAwardedShields(HashMap<Players,Integer> targetedPlayers) {
        for(Map.Entry<Players,Integer> e : targetedPlayers.entrySet()) {
            e.getKey().awardShields(e.getValue());
            LOG.info("Player "+e.getKey().getName()+" gains "+e.getValue()+" shields!");
        }
    }

    /**
     * Reduces a group of players shields by a specified amount. If a player does not have enough shields
     * they will be reduced to zero.
     *
     * @param targetedPlayers A map listing the players affected and the number of shields they must loose.
     * @return A map of the players affected with a boolean value. It will be True only if that player lost at least one shield.
     */
    public HashMap<Players,Boolean> playerLoosesShieldsHashMap(HashMap<Players,Integer> targetedPlayers) {
        HashMap<Players,Boolean> results = new HashMap<>();
        for(Map.Entry<Players,Integer> e : targetedPlayers.entrySet()) {
            results.put(e.getKey(),e.getKey().looseShields(e.getValue()));
            LOG.info("Player "+e.getKey().getName()+" looses "+e.getValue()+" shields!");
        }
        return results;
    }

    /**
     * Force a player to discard a number of cards with respect to subtype.
     * This only initiates a sequence which forces the player to fufill this list, and calls a
     * resolver once the discard criteria has been met.
     * @param effect The effect that is initiating this action
     * @param targetedPlayers A list of target players mapped to a Map that indicates how many cards of each subtype to discard
     * @return True if the effect was successfully initiated. It will be resolved only once the target discards the necessary cards.
     */
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
            String discardMessage="You Must Discard the following cards from your hand!\n";
            for(Map.Entry<CardTypes,Integer> t : e.getValue().entrySet()) {
                discardMessage += e.getKey()+" : "+e.getValue()+"\n";
            }
            NotificationOutbound notificationPlayer = new NotificationOutbound(effect.getSource().getCardName(),discardMessage,effect.getSource().getCard().getImgSrc(),null);

            NotificationOutboundService.getService().sendBadNotification(effect.getActivatedBy(),notificationPlayer,null);
            obList.add(ob);
            rc=true;
        }
        HashSet<Players> otherList = new HashSet<>(getPlayerList());
        otherList.removeAll(targetedPlayers.keySet());
        NotificationOutbound toOthers = new NotificationOutbound(effect.getSource().getCardName(),"Waiting for other players to discard cards!",effect.getSource().getCard().getImgSrc(),null);
        NotificationOutboundService.getService().sendInfoNotification(effect.getActivatedBy(),toOthers,null);
        return rc;
    }

    /**
     * Forces a player to discard a number of cards from hand, without respect to card subtype.
     * This only initiates a sequence which forces the player to fufill this list, and calls a
     * resolver once the discard criteria has been met.
     * @param effect The effect that is initiating this action.
     * @param targetPlayer The player targeted by the effect.
     * @param numCards The number of cards the target must discard
     * @return True if the effect was successfully initiated. It will be resolved only once the target discards the necessary cards.
     */
    public boolean forcePlayerDiscards(Effects effect, Players targetPlayer, int numCards) {
        if(effect==null || targetPlayer==null || numCards <1) {
            LOG.warn("Effect %s tried to use function forcePlayerDiscards but sent invalid args. This was refused");
            throw new RuntimeException(String.format("EffectResolverService cannot execute forcePlayerDiscards(Effect=%s,Players=%s,int=%d) with invalid args.",effect,targetPlayer.getName(),numCards));
        }
        DiscardObserver ob = new DiscardObserver(effect,targetPlayer,numCards);
        HashSet<DiscardObserver> obList = activeDiscardObservers.get(effect);
        targetPlayer.getHand().registerDiscardObserver(ob);
        String discardMessage=String.format("You Must Discard %d cards from your hand!",numCards);
        NotificationOutbound notificationPlayer = new NotificationOutbound(effect.getSource().getCardName(),discardMessage,effect.getSource().getCard().getImgSrc(),null);
        NotificationOutboundService.getService().sendBadNotification(effect.getActivatedBy(),notificationPlayer,null);
        if(obList==null) {
            obList = new HashSet<>();
            activeDiscardObservers.put(effect,obList);
        }
        NotificationOutbound toOthers = new NotificationOutbound(effect.getSource().getCardName(),"Waiting for other players to discard cards!",effect.getSource().getCard().getImgSrc(),null);
        for(Players p : getPlayerList()) {
            if(p!=targetPlayer) {
                NotificationOutboundService.getService().sendInfoNotification(p,toOthers,null);
            }
        }
        obList.add(ob);
        return true;
    }

    /**
     * Awards a group of players a specified amount of adventure cards, drawing them into their hand.
     *
     * @param targetedPlayers A map indicating the affected players associated with how many cards to award that player.
     */
    public void drawAdventureCards(HashMap<Players,Integer> targetedPlayers) {
        for(Map.Entry<Players,Integer> e : targetedPlayers.entrySet()) {
            for(int i=0;i<e.getValue();++i) {
                gameController.getADeck().drawCard(e.getKey().getHand());
            }
            LOG.info("Player "+e.getKey().getName()+" draws "+e.getValue()+" adventure cards!");
        }
    }

    /**
     * Discards all cards in play for a group of players.
     *
     * @param targetedPlayers A set of players that are affected.
     * @return A map of the affected players with associated boolean values that are true if at least one card was discarded in this way.
     */
    public HashMap<Players,Boolean> playerDiscardsAllCardsFromPlay(HashSet<Players> targetedPlayers) {
        return playerDiscardsAllCardsFromPlay(targetedPlayers,null);
    }

    /**
     *
     * @param targetedPlayers
     * @param cardTypeList
     * @return
     */
    /**
     * Discards all cards in play for a group of players.
     *
     * @param targetedPlayers A set of players that are affected.
     * @param cardTypeList A set of card subtypes that will be targeted by the discard effect
     * @return A map of the affected players with associated boolean values that are true if at least one card was discarded in this way.
     */
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

    /**
     * Registers an effect that requires a target list composed of quest victors. This will suspend the
     * effects execution until the next quest is completed.
     * @param effect The effect that must be suspended until the next quest completes
     */
   public void registerEffectTriggeredOnQuestCompleted(Effects effect) {
        LOG.info("Register triggered card effect "+effect.source.getCardCode());
        triggeredEffects.add(effect);
    }

    /**
     * Unregister an effect that was waiting for the next quest to complete. This is only used when an effect
     * is reset and its execution interrupted.
     *
     * @param effect The effect to be unregistered
     */
   public void unregisterEffectTriggeredOnQuestCompleted(Effects effect) {
       LOG.info("UN-Register triggered card effect "+effect.source.getCardCode());
        triggeredEffects.remove(effect);
    }

    /**
     * Suspends the execution of an effect and informs the affected players to provide a target for the effect.
     * The suspended effect will be triggered again once the affected players provided the target and the effect
     * has been resolved. The type of resolution is dependent on the TargetSelectionRequestType provided.
     * @param effect The effect that initiated this action that will be resumed once resolved.
     * @param type The type of target selection required. This will determine what action occurs after the target is provided.
     * @param requestMessage The notification message to be displayed to the player indicating what action they must take.
     * @return A unique requestID is returned that represents this request and ensures the response and effect match the original request.
     */
    public long targetSelectionRequest(Effects effect, TargetSelectionRequestTypes type,String requestMessage) {
        if(effect==null) {
            return -1;
        }
        else if(type==TargetSelectionRequestTypes.STAGE_TARGET_SELECTION && questController==null)
        {
            throw new IllegalEffectStateException("targetSelection cannot occur without an active quest phase.",effect,effect.getSource());
        }
        long requestID = nextRequestID++;
        TargetSelectionRequest msg = new TargetSelectionRequest(
          requestID,
          effect.getActivatedBy().getPlayerId(),
          effect.getSource().getCardCode(),
          type
        );
        NotificationOutbound notification = new NotificationOutbound(
          effect.getSource().getCard().getCardName()+": SELECT Target",
          requestMessage,
          effect.getSource().getCard().getImgSrc(),
                null
        );
        notifyService.sendInfoNotification(effect.getActivatedBy(),notification,null);
        OutboundService.getService().sendTargetSelectionRequest(msg,effect.getActivatedBy());
        targetSelectionRequestIdToEffects.put(requestID,effect);
        return requestID;
    }

    /**
     * Must be called when quest phase is initiated to enable
     * @param questController
     */
    public void onQuestPhaseStarted(QuestPhaseController questController) {
        this.questController = questController;
    }

    /**
     * Receives a list of quest victors mapped to the shields to be rewarded for the associated player.
     * This player list is then forwarded to any effects that are waiting on the next quest completion
     * so they can be resolved.
     *
     * @param targetedPlayers A map of players who have completed the quest with their associated shield rewards.
     */
    public void onQuestCompleted(HashMap<Players, Integer> targetedPlayers) {
        if(targetedPlayers==null || targetedPlayers.isEmpty()) {
            return;
        }
        LOG.info("onQuestCompleted triggered");
        playerAwardedShields(targetedPlayers);
        ArrayList<Players> questVictors = new ArrayList<>(targetedPlayers.keySet());
        for(Effects e : triggeredEffects) {
            e.trigger(questVictors);
        }
    }

    /**
     * When a player is forced to discard cards a discard observer is placed on their hand that counts cards that
     * match the discard criteria until that criteria is met. Once the critera is met this function will be called
     * by the observer. This function will then remove the observer from that effect's list of observers. If all
     * observers have been resolved the effect will then be resumed via its trigger.
     * @param observer The discard observer that has been resolved.
     */
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

    /**
     * Triggered when the client provides a target selection for a stage in response to another request initiated
     * by an effect through the effect resolver service.
     *
     * This will validate the response matches the original request then resolve the effect by telling the QuestController to
     * reveal the targeted stage.
     *
     * @param data The stage target response sent from the client indicating the stageID targeted for this effect.
     * @return True if successful, False if the stage was not revealed either because the stageID was not found, or the source card had already been used during this quest (Max once per quest phase)
     */
    public boolean handleStageTargetSelectionResponse(StageTargetSelectionResponse data) {
        if(questController==null) {
            throw new RuntimeException("handleStageTargetSelectionResponse cannot be called when there is no current quest stage set.");
        }
        Effects effect = targetSelectionRequestIdToEffects.get(data.requestID());
        if(effect==null) {
            LOG.warn(String.format("StageTargetSelectionResponse received an unrecognized requestID %d and was rejected.",data.requestID()));
            return false;
        }
        else if(data.requestPlayerID()!=effect.activatedBy.getPlayerId()) {
            LOG.warn(String.format("StageTargetSelectionResponse failed because requesting player (%d) does not match player that made request (%d)",data.requestPlayerID(),effect.activatedBy.getPlayerId()));
            return false;
        }

       boolean rc = questController.makeStageVisibleToPlayer(data.targetStageID(),effect.getActivatedBy(),effect.source.getCardID());

        if(rc) {
            targetSelectionRequestIdToEffects.remove(data.requestID());
            effect.trigger(data.requestID());
        }
        return rc;
    }

    /**
     * Triggered when the client provides a target selection for a card in response to another request initiated
     * by an effect through the effect resolver service.
     *
     * This will validate the response matches the original request then resolve the effect by making sure the targeted
     * player exists and then attempting to destroy that card from the players play area.
     *
     * @param data The card target reponse provided by the client indicating the target playerID and target cardID for the effect
     * @return True if the card was destroyed, False if the card was not destroyed because either the player or card couldn't be found.
     */
    public boolean handleCardTargetSelectionResponse(CardTargetSelectionResponse data) {
        Players targetPlayer = gameController.findPlayerWithID(data.targetPlayerID());
        if(targetPlayer==null) {
            return false;
        }
        Effects effect = targetSelectionRequestIdToEffects.get(data.requestID());
        if(effect==null) {
            LOG.warn(String.format("CardTargetSelectionResponse received an unrecognized requestID %d and was rejected.",data.requestID()));
            return false;
        }
        else if(data.requestPlayerID()!=effect.activatedBy.getPlayerId()) {
            LOG.warn(String.format("CardTargetSelectionResponse failed because requesting player (%d) does not match player that made request (%d)",data.requestPlayerID(),effect.activatedBy.getPlayerId()));
            return false;
        }

        boolean rc = targetPlayer.getPlayArea().destroyAllyCard(data.targetCardID());

        if(rc) {
            String msg = String.format("Mordred has cast a spell and destroyed one of %s's allies!",targetPlayer.getName());
            targetSelectionRequestIdToEffects.remove(data.requestID());
            effect.trigger(data.requestID());
            NotificationOutbound msgOut = new NotificationOutbound(effect.getSource().getCardName(),msg,effect.getSource().getCard().getImgSrc(),null);
            NotificationOutboundService.getService().sendGoodNotification(effect.getActivatedBy(),msgOut,null);
            NotificationOutboundService.getService().sendBadNotification(targetPlayer,msgOut,null);
            for(Players p : getPlayerList()) {
                if(p!=effect.getActivatedBy() || p!=targetPlayer) {
                    NotificationOutboundService.getService().sendInfoNotification(p,msgOut,null);
                }
            }
        }
        return rc;
    }

    /**
     * Used Spring-boot API once singleton is created. This sets the singleton instance to the static variable.
     *
     * @param applicationContext The application context for this sprint-boot application. It holds a reference to all spring manged components.
     * @throws BeansException If the requested application context was not found.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * For use by non-spring managed classes to access this spring managed component via singleton approach.
     * This is necessary over application context methods in constructors because it doesn't force spring to
     * create the component until it's required. Failure to use this approach can cause circular dependency
     * errors in spring from non-spring managed components.
     * @return The spring managed instance of this class.
     */
    public static EffectResolverService getService() {
        return context.getBean(EffectResolverService.class);
    }
}
