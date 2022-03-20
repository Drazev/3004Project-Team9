package com.team9.questgame.gamemanager.service;

import com.team9.questgame.Data.*;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.StoryCards;
import com.team9.questgame.gamemanager.record.rest.EmptyJsonReponse;
import com.team9.questgame.gamemanager.record.socket.HandUpdateOutbound;
import com.team9.questgame.gamemanager.record.socket.PlayerNextTurnOutbound;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@AllArgsConstructor
@Service
public class OutboundService implements ApplicationContextAware {
    private Logger LOG;

    @Autowired
    private SimpMessagingTemplate messenger;

    @Autowired
    private SessionService sessionService;

    private static ApplicationContext context;

    public OutboundService() {
        this.LOG = LoggerFactory.getLogger(OutboundService.class);
    }

    /**
     * Broadcast player-draw-connect event
     */
    public void broadcastPlayerConnect() {
        Map<String, String> players = sessionService.getPlayers();
        this.sendToAllPlayers("/topic/general/player-connect", players);
    }

    public void broadcastPlayerDisconnect() {
        Map<String, String> players = sessionService.getPlayers();
        this.sendToAllPlayers("/topic/general/player-disconnect", players);
    }

    public void broadcastGameStart() {
        // @TODO: include player turn in the next iteration
        this.sendToAllPlayers("/topic/general/game-start");
    }

    public void broadcastNextTurn(Players player) {
        // @TODO: include player turn in the next iteration
        this.sendToAllPlayers("/topic/general/next-turn", new PlayerNextTurnOutbound(player.getPlayerId(), player.getName()));
    }

    public void broadcastHandUpdate( Players sourcePlayer, HandData toUser, HandData toOthers) {
        final String topic = "/topic/player/hand-update";
        sendToPlayer(topic,sourcePlayer,toUser);
        sendToAllExceptPlayer(topic,toOthers,sourcePlayer);

    }

    public void broadcastPlayerDataChanged(Players player,PlayerData playerData) {
        LOG.info(String.format("Broadcasting \"player-update\" for player: %s",player.getName()));
        this.sendToAllPlayers("/topic/player/player-update", playerData);
    }

    private void sendToPlayer(String topic, Players player, Object payload) {
        LOG.info(String.format("Broadcasting to one player: topic=%s, name=%s, payload=%s", topic, player.getName(), payload));
        messenger.convertAndSendToUser(sessionService.getPlayerSessionId(player), topic, payload);
    }

    public void broadcastPlayAreaChanged(Players player, PlayAreaData data) {
        LOG.info(String.format("Broadcasting \"play-area-changed\", Source: %s, ID: %d",player.getName(),player.getPlayerId()));
        this.sendToAllPlayers("/topic/play-areas/play-area-changed",data);
    }

    public void broadcastHandOversize(Players player, HandOversizeData data) {
        LOG.info(String.format("Broadcasting \"hand-oversize\", Source: %s, ID: %d",player.getName(),player.getPlayerId()));
        this.sendToAllPlayers("/topic/player/hand-oversize",data);
    }

    public void broadcastDeckUpdate(DeckUpdateData data) {
        LOG.info(String.format("Broadcasting \"deck-update\" for deck %s",data.deckType()));
        this.sendToAllPlayers("/topic/decks/deck-update",data);
    }

    public void broadcastStoryCard(CardData storyCardData) {
        this.sendToAllPlayers("/topic/general/player-draw-card", storyCardData);
    }

    private void sendToPlayer(String topic, String name, Object payload) {
        LOG.info(String.format("Broadcasting to one player: topic=%s, name=%s, payload=%s", topic, name, payload));
        messenger.convertAndSendToUser(sessionService.getPlayerSessionId(name),topic, payload);
    }

    private void sendToAllPlayers(String topic, Object payload) {
        LOG.info(String.format("Broadcasting to all players: topic=%s, payload=%s", topic, payload));
        messenger.convertAndSend(topic, payload);
    }
    private void sendToAllPlayers(String topic) {
        LOG.info(String.format("Broadcasting to one players: topic=%s", topic));
        messenger.convertAndSend(topic, new EmptyJsonReponse());
    }

    private void sendToAllExceptPlayer(String topic, Object payload, Players excludedPlayer) {
        LOG.info(String.format("Selective Broadcast to all players except {name: "+excludedPlayer.getName()+", PlayerID: "+excludedPlayer.getPlayerId()+"}, Topic: "+topic+" Payload: "+payload));
        for(Map.Entry<Players,String> e : sessionService.getPlayerToSessionIdMap().entrySet()) {
            if(e.getKey()!=excludedPlayer) {
                messenger.convertAndSendToUser(e.getValue(),topic,payload);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static OutboundService getService() {
        return context.getBean(OutboundService.class);
    }

}
